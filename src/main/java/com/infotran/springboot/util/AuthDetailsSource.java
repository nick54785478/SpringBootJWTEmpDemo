package com.infotran.springboot.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.infotran.springboot.entity.AuthDetails;

@Component
public class AuthDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, AuthDetails> {

	@Override
	public AuthDetails buildDetails(HttpServletRequest context) {

		return new AuthDetails(context);
	}

}
