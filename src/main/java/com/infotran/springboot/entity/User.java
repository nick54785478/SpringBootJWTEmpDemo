package com.infotran.springboot.entity;

/**
 * 繼承自UserInfo 新增角色欄位、新密碼欄位
 */

public class User extends UserInfo {

	private static final long serialVersionUID = 1L;

	private String role;
	private String newpassword;
	private final String PREFIX = "ROLE_";

	public String getRole() {
		return PREFIX+role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getPREFIX() {
		return PREFIX;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}