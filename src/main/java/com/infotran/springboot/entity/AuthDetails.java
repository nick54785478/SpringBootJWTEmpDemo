package com.infotran.springboot.entity;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 用來獲取登錄時的額外信息: 如: remoteAddress、sessionId
 */
public class AuthDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = 1L;
	private Map<String, Object> params;

	public AuthDetails(HttpServletRequest request) {
		super(request);
	}

	public AuthDetails(HttpServletRequest request, Map<String, Object> params) {
		super(request);
		this.params = params;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
