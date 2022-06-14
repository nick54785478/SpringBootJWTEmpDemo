package com.infotran.springboot.module.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.infotran.springboot.util.Response;

/**
 * 登入失敗處理
 */
@Component
public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginFailHandler.class);

	// 使用者帳號或密碼錯誤
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		request.setCharacterEncoding("UTF-8");
		// 獲取使用者名稱
		String username = request.getParameter("username"); // 請求參數名稱

		// 設定HTTP強制安全傳輸技術(HTTP Strict Transport Security，HSTS) header
		response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		// 寫入logback
		log.error(" 使用者名稱或密碼錯誤。", exception);
		out.write(new Response("00101", "使用者名稱或密碼錯誤。").toString());
		out.flush();
		out.close();
	}
}
