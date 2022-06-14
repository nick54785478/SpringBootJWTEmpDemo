package com.infotran.springboot.module.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.infotran.springboot.entity.UserInfo;
import com.infotran.springboot.util.JwtTokenUtil;
import com.infotran.springboot.util.Response;

/**
 * 登入成功後的後續驗證處理
 *
 */

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginSuccessHandler.class);

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	// 當使用者帳號和密碼正確
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 從Context中取得使用者資訊
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		log.info("登入成功，從SecurityContextHolder中獲取使用者資訊...");

		if (principal != null && principal instanceof UserDetails) {
			UserInfo user = (UserInfo) principal;

			log.info("user: " + user.getAuthorities());
//                 	System.out.println("user: "+user.getAuthorities());
			request.getSession().setAttribute("user", user); // 存入Session
			request.getSession().setAttribute("departmentid", user.getDeptId());
			request.getSession().setAttribute("username", user.getUsername());

			log.info("principal: " + principal.toString());

			String role = "";
			Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

			log.info("authorities: ");
			for (GrantedAuthority authority : authorities) {
				role = authority.getAuthority(); // ROLE_角色名
				log.info("role = " + role);
			}

			request.getSession().setAttribute("role", role);
			log.info("使用者資訊: { Username : " + user.getUsername() + ", Role : " + role + " }");

			// 建立Token
			String token = jwtTokenUtil.createToken(user.getUsername(), role, user.getDeptId()); // 建立Token
			log.info("建立JWT Token ");
			// 依照JWT的規定，最後請求時應該是"Bearer token"
			response.setHeader("token", jwtTokenUtil.TOKEN_PREFIX + token);
			response.setContentType("application/json;charset=utf-8");
			// 設定HTTP強制安全傳輸技術(HTTP Strict Transport Security，HSTS) header
			response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

			// 印出結果
			PrintWriter pw = response.getWriter();
			pw.write("Hello " + user.getName());
			pw.write("\n");
			pw.write(new Response("200", "登入成功").toString());
			pw.write("\n");
//                 	out.write("{\"status\":\"ok\",\"message\":\"登入成功\"}");
			pw.write("{ \n \"token\" : \"" + token.toString() + "\" \n }");
			pw.flush();
			pw.close();
		}
	}

}
