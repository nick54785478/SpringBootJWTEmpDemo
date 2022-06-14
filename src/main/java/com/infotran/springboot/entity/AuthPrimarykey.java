package com.infotran.springboot.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * JavaBean Role_Auth 的複合主鍵
 * 
 * @param String deptId;
 * @param String roleId;
 * @param String funcId; 註: 代表複合主鍵的實體bean需要實現Serializable介面
 */

@Embeddable
public class AuthPrimarykey implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "dept_id")
	private String deptId;

	@Column(name = "role_id")
	private String roleId;

	@Column(name = "func_id")
	private String funcId;

	public AuthPrimarykey() {
	}

	public AuthPrimarykey(String deptId, String roleId, String funcId) {
		this.deptId = deptId;
		this.roleId = roleId;
		this.funcId = funcId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = "ROLE_" + roleId;
	}

	public String getFuncId() {
		return funcId;
	}

	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	@Override
	public String toString() {
		return "AuthPrimarykey [deptId=" + deptId + ", roleId=" + roleId + ", funcId=" + funcId + "]";
	}

}
