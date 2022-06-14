package com.infotran.springboot.module.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.infotran.springboot.util.Response;

/**
 * 權限不足例外處理
 *
 */

@Component
public class DeniedHandler implements AccessDeniedHandler {

	private static final Logger log = LoggerFactory.getLogger(DeniedHandler.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		// 設定HTTP強制安全傳輸技術(HTTP Strict Transport Security，HSTS) header
		response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		// 寫入logback
		log.error(" 權限不足，拒絕存取! " + accessDeniedException);
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setHeader("Content-Type", "application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write(new Response("00103", "權限不足，拒絕存取!").toString());
//         	out.write("{\"code\":\"00103\", \"message\":\"權限不足，拒絕存取!\"}");
		out.flush();
		out.close();

	}

}
