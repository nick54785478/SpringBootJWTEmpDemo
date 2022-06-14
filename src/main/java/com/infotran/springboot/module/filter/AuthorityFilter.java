package com.infotran.springboot.module.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.infotran.springboot.entity.Func;
import com.infotran.springboot.service.Impl.UserFuncServiceImpl;
import com.infotran.springboot.util.ExceptionMsg;
import com.infotran.springboot.util.JwtTokenUtil;
import com.infotran.springboot.util.Response;

@Component
@Order(2)
public class AuthorityFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(AuthorityFilter.class);

	@Autowired
	JwtTokenUtil jwtTokenUtil;
	@Autowired
	UserFuncServiceImpl ufService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(jwtTokenUtil.TOKEN_HEADER);
		String httpMethod = request.getMethod(); // 請求方法
		String contextPath = request.getContextPath(); // ContextPath: /SpringSecurityDemo
		String servletPath = request.getServletPath(); // servletPath: 後續路徑
		log.warn("######### 進入AuthorityFilter #########");


		
		// 若request header中沒有Authorization訊息則直接放行(第一次登入)
		if (header == null || !header.startsWith(jwtTokenUtil.TOKEN_PREFIX)) {
			filterChain.doFilter(request, response); // 過濾器繼續往下走
			return;
		}

		log.info("  從SecurityContextHolder獲取當前用戶信息");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, String> details = (Map<String, String>) authentication.getDetails();
		String role = details.get("role"); // 取出角色身分及其所對應的權限
		log.info("  身分: " + role + " , 資訊: " + details);

		// root直接放行
		if ("ROLE_root".equals(role)) {
			log.warn("######### 身分為root，直接放行，離開AuthorityFilter #########");
//     	System.out.println("root直接放行!!!!");
			filterChain.doFilter(request, response);
			return;
		}

		Func authFunc = ufService.getBySrvltPath(servletPath); // 相關權限功能
		log.info("authFunc = " + authFunc);
		// 此路徑是否取得出相關權限功能
		if (authFunc != null) {
			// 是否permitAll
			Boolean isPermitAll = "Y".equals(authFunc.getPermitAll()) ? true : false;
			log.info("isPermitAll = " + isPermitAll);
			if (isPermitAll) {
				log.info(contextPath + servletPath + "為permitAll，直接放行 ");
				filterChain.doFilter(request, response);
				return;
			}

		}

		log.warn("HttpMethod: " + httpMethod + ", Path: " + contextPath + servletPath);

		// 依路徑搜尋可登入的角色
		log.info("依路徑搜尋可登入的角色: ");

		List<String> authList = ufService.getAuthoritiesBySrvltPath(servletPath);
		log.info("perList is Empty?: " + authList.isEmpty());

		if (!authList.isEmpty()) {
			for (String auth : authList) {
				if (auth.equals(role)) {
					log.warn("######### 角色" + auth + "權限相符，予於放行，離開AuthorityFilter#########");
					filterChain.doFilter(request, response);
					return;
				}

				log.warn("角色" + auth + "與角色" + role + "不相符，拒絕放行");

			}
		}

		// 設定HTTP強制安全傳輸技術(HTTP Strict Transport Security，HSTS) header
		response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();

		log.error("未被AuthorityFilter放行，發生錯誤!!");
		Response resMsg = new Response(new ExceptionMsg("00103", "權限不足，拒絕存取!"));
		out.write(resMsg.toString());
		out.flush();
		out.close();

	}

}
