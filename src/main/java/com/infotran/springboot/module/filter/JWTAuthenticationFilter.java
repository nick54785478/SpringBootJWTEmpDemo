package com.infotran.springboot.module.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.infotran.springboot.entity.AuthDetails;
import com.infotran.springboot.util.ExceptionMsg;
import com.infotran.springboot.util.JwtTokenUtil;
import com.infotran.springboot.util.Response;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
@Order(1)
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
	// OncePerRequestFilter由 Spring 提供的，能加強確保後端接收到一個請求，該 Filter 只會執行一次。

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	/**
	 * doFilterInternal 收到請求時，Filter 會自動執行此方法。 參數包含: 請求（HttpServletRequest）。
	 * 回應（HttpServletResponse）。 過濾鏈（FilterChain）。 註: FilterChain 會將現有的
	 * Filter給串連起來，當請求進入後端，需要依序經過它們才會到達 Controller。 相對地，當回應離開
	 * Controller，則是按照相反的順序經過那些 Filter。
	 */

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String httpMethod = request.getMethod(); // 請求方法
		String contextPath = request.getContextPath(); // ContextPath: /SpringSecurityDemo
		String servletPath = request.getServletPath(); // servletPath: 後續路徑

		log.warn("######### 進入JWTAuthenticationFilter #########");
		log.info("HttpMethod: " + httpMethod + " http://localhost:8080/" + contextPath + servletPath);

		try {
			// Bearer + Token
			String header = request.getHeader(jwtTokenUtil.TOKEN_HEADER); // 獲取標頭Header(有Token)
			log.info("Header = " + header);
			String tokenBody = header.replace(jwtTokenUtil.TOKEN_PREFIX, ""); // 若 header = null 則此行程式碼會出錯，意味著沒有Token

			// 若request header中沒有Authorization訊息則直接放行(第一次登入)
			if (header == null || !header.startsWith(jwtTokenUtil.TOKEN_PREFIX)) {
				filterChain.doFilter(request, response); // 過濾器繼續往下走
				return;
			}

			// 在 Filter 中查詢使用者的目的，是為了將該次請求所代表的驗證後資料（Authentication）帶進 Security 的「Context」。
			// Context 是一種較抽象的概念，在此可以想像成該次請求的身份狀態。
			log.info("驗證使用者攜帶的Token ");
			UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response, header);
			log.info("Authentication = " + authentication);
			log.info("detail = " + authentication.getDetails());


			// 解析Token
			Map<String, Object> parsedToken = jwtTokenUtil.parseToken(tokenBody);
			log.info("parsedToken = " + parsedToken);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("在SecurityContextHolder上設置Authentication ");
			log.info(" * Authentication = " + authentication);

			log.warn("######### 予於放行，離開JWTAuthenticationFilter #########");

			super.doFilterInternal(request, response, filterChain);

		} catch (ExpiredJwtException e) {// Token 是否失效
			log.error("Token已經過期", e);
			// 設定HTTP強制安全傳輸技術(HTTP Strict Transport Security，HSTS) header
			response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
			response.setContentType("application/json;charset=utf-8");

			Response resMsg = new Response(new ExceptionMsg("00110", "Token已過期，請重新登入!"));
			// 印出結果
			PrintWriter out = response.getWriter();
			out.write(resMsg.toString());
			out.flush();
			out.close();

		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {

			log.error("Token驗證例外拋出: ", e);
			// 設定HTTP強制安全傳輸技術(HTTP Strict Transport Security，HSTS) header
			response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
			response.setContentType("application/json;charset=utf-8");
			Response resMsg = new Response(new ExceptionMsg("00111", "Token驗證不符，拒絕存取!"));
			PrintWriter out = response.getWriter();
			out.write(resMsg.toString());
			// out.write("{\"code\":\"00111\",\"message\":\"Token驗證不符，拒絕存取!\"}");
			out.flush();
			out.close();

		} catch (NullPointerException e) {
			log.error("未攜帶Token，驗證發生錯誤: ", e);
			// 設定HTTP強制安全傳輸技術(HTTP Strict Transport Security，HSTS) header
			response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
			response.setContentType("application/json;charset=utf-8");

			Response resMsg = new Response(new ExceptionMsg("00112", "未攜帶Token，驗證發生錯誤"));
			PrintWriter out = response.getWriter();
			out.write(resMsg.toString());
			out.flush();
			out.close();
		}

		// 若有取得Token的Principal
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {

			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 取得principal
			log.info("header: principal => "
					+ SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
			log.info("header: authorities => "
					+ SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());

		}

	}

	// 驗證token，並生成驗證後的Token
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
			HttpServletResponse response, String header) throws ServletException, IOException {
		String token = header.replace(jwtTokenUtil.TOKEN_PREFIX, "");
		String username = jwtTokenUtil.getUsername(token); // 從Token中取得使用者名稱
		String role = jwtTokenUtil.getUserRole(token); // 從Token中取得角色
		System.out.println("jwtTokenUtil.getUserRole(token): " + jwtTokenUtil.getUserRole(token));
		Boolean isExpiration = jwtTokenUtil.isExpiration(token); // Token是否過期
		System.out.println("取出簽發日: " + jwtTokenUtil.getIssAt(token));
		System.out.println("取出的過期日: " + jwtTokenUtil.getExpDate(token));

		Map<String, Object> claims = jwtTokenUtil.getTokenBody(token);

		// 若過期日期在現在日期之前，則Token過期(判別用)
		if (jwtTokenUtil.getExpDate(token).before(new Date()) || jwtTokenUtil.isExpiration(token)) {
			log.warn("Token是否過期: " + isExpiration);
			isExpiration = true;

			log.warn("Token 已經過期，請再重新登入...");
		}

		log.warn("Token 是否過期: " + isExpiration);

		if (username != null) {
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null,
					Collections.singleton(new SimpleGrantedAuthority(role)));

			AuthDetails authDetails = new AuthDetails(request);
			claims.put("RemoteAddress", authDetails.getRemoteAddress()); // RemoteAddress客戶端ip
			claims.put("SessionId", authDetails.getSessionId()); // Session id
			// 設置Detail
			authToken.setDetails(claims);

			return authToken;
		}

		log.warn("getAuthentication()方法回傳null");
		return null;
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
