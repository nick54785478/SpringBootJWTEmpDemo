package com.infotran.springboot.util;

/**
 * 自訂例外訊息 繼承 RuntimeException
 * @param String code - 錯誤碼
 * @param String msg - 例外訊息
 */
public class ExceptionMsg extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String code;

	public ExceptionMsg(String code, String msg) {
		super(msg);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
