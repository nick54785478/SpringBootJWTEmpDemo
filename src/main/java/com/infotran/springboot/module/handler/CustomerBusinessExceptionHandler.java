package com.infotran.springboot.module.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.infotran.springboot.util.ExceptionMsg;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

/**
 * 自訂全域捕捉例外(自定義例外處理) SpringSecurity未提供hadnler的例外在此定義並處理。 註.
 * *僅能捕捉Controller拋出的錯誤，filter拋出的無法捕捉
 */

@ControllerAdvice
public class CustomerBusinessExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(CustomerBusinessExceptionHandler.class);

	/**
	 * 自訂業務處理例外類別
	 */
	@ResponseBody
	@ExceptionHandler(ExceptionMsg.class)
	public Map<String, Object> bussinessExceptionHandler(ExceptionMsg e) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", e.getCode());
		map.put("message", e.getMessage());

		return map;
	}

	/**
	 * UsernameNotFoundException例外處理
	 */
	@ResponseBody
	@ExceptionHandler(UsernameNotFoundException.class)
	public Map<String, Object> usernameNotFoundExceptionHandle(final UsernameNotFoundException e) {
		// 寫入logback
		log.error("查不到用戶資料: " + e);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "00102");
//         	map.put("message", e.getMessage());       	
		map.put("message", "用戶不存在");
		return map;
	}

	@ResponseBody
	@ExceptionHandler(ExpiredJwtException.class)
	public Map<String, Object> ExpiredJwtExceptionHandle(final ExpiredJwtException e) {
		log.error("Token過期" + e);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "00110");
		map.put("message", "Token已過期，請重新登入!");
		return map;

	}

	@ResponseBody
	@ExceptionHandler(SignatureException.class)
	public Map<String, Object> SignatureExceptionHandler(final SignatureException e) {
		log.error("簽名有誤" + e);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "00111");
		map.put("message", "Token驗證不符，拒絕存取!");
		return map;

	}

}
