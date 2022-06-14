package com.infotran.springboot.util;

// 自訂例外訊息
public enum ExceptionMsg2Enum {

	USERERROR("00001", "用戶端錯誤。"), REGISTERERROR("00100", "用戶註冊錯誤。"), USERNAMEORPASSWORDERROR("00101", "使用者名稱或密碼錯誤。"),
	USERNAMENOTFOUND("00102", "查無此用戶。"), ACCESSDENIED("00103", "權限不足，拒絕存取!"), PASSWORDNOTMATCH("00104", "密碼不一致，修改失敗!"),
	TOKENEXPIRED("00110", "Token已過期，請重新登入!"), TOKENDENIED("00111", "Token驗證不符，拒絕存取!"),
	TOKENNOTFOUND("00112", "未攜帶Token，驗證發生錯誤"), UNKNOWNERROR("00000", "發生未知錯誤");

	private ExceptionMsg2Enum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	private final String code;
	private final String message;

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public static ExceptionMsg2Enum getExceptionMsg(String e) {
		ExceptionMsg2Enum eMsg = ExceptionMsg2Enum.valueOf(e);
		if (eMsg != null) {
			return eMsg;
		}

		return ExceptionMsg2Enum.getExceptionMsg("UNKNOWNERROR");

	}
}
