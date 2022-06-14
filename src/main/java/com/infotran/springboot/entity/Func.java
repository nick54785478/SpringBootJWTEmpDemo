package com.infotran.springboot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

/**
 * 功能描述表
 * @param String  funcId - 功能代號;
 * @param String  funcType - 功能類型;
 * @param String  funcName - 功能名稱;
 * @param String  servletPath - ServletPath;
 * @param String  dispOrder - 排列順序;
 * @param Boolean permitAll - 是否允許所有人存取
 */

@Entity
@Table(name = "EmpFunc")
public class Func {

	@Id
	@Column(name = "func_id")
	@Type(type = "string")
	private String funcId;
	@Column(name = "func_type")
	private String funcType;
	@Column(name = "func_name")
	private String funcName;
	@Column(name = "servlet_path")
	private String servletPath;
	@Column(name = "disp_order")
	private String dispOrder;
	@Column(name = "permit_all")
	private String permitAll;
	@Transient
	private static final String PREFIX = "ROLE_";

	public Func() {
	}

	public Func(String funcId, String funcType, String funcName, String servletPath, String dispOrder,
			String permitAll) {
		this.funcId = funcId;
		this.funcType = funcType;
		this.funcName = funcName;
		this.servletPath = servletPath;
		this.dispOrder = dispOrder;
		this.permitAll = permitAll;
	}

	public String getFuncId() {
		return funcId;
	}

	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	public String getFuncType() {
		return funcType;
	}

	public void setFuncType(String funcType) {
		this.funcType = funcType;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getServletPath() {
		return servletPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	public String getDispOrder() {
		return dispOrder;
	}

	public void setDispOder(String dispOrder) {
		this.dispOrder = dispOrder;
	}

	public static String getPrefix() {
		return PREFIX;
	}

	public String getPermitAll() {
		return permitAll;
	}

	public void setPermitAll(String permitAll) {
		this.permitAll = permitAll;
	}

	public void setDispOrder(String dispOrder) {
		this.dispOrder = dispOrder;
	}
}
