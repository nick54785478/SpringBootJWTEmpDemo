package com.infotran.springboot.util;

/**
 * Response
 * 
 * @param String rspCode - 狀態代碼
 * @param String rspMsg - 訊息
 */
public class Response {

	String rspCode;
	String rspMsg;

	public Response(ExceptionMsg exMsg) {
		this.rspCode = exMsg.getCode();
		this.rspMsg = exMsg.getMessage();
	}

	public Response(String rspCode, String rspMsg) {
		this.rspCode = rspCode;
		this.rspMsg = rspMsg;
	}

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspMsg() {
		return rspMsg;
	}

	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}

	@Override
	public String toString() {
		return "{\"code:\":\"" + rspCode + "\",\"message\":\"" + rspMsg + "\"}";
	}

}
