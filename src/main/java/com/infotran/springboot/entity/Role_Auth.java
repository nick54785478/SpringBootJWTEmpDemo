package com.infotran.springboot.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色權限表
 * 
 * @param AuthPrimarykey authPrimarykey - 複合主鍵(由 deptId、roleId、funcId 組成)
 * @param String         creflag - 新增允許;
 * @param String         inqflag - 查詢允許;
 * @param String         updflag - 修改允許;
 * @param String         delflag - 刪除允許;
 * @param String         procflag - 處理允許;
 */

@Entity
@Table(name = "Emp_role_auth")
public class Role_Auth implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 複合主鍵 註: 不能用@Id，需要用@EmbeddedId。插入資料的時候必須手工賦值， 複合主鍵的JavaBean須註明@Embeddable
	 */
	@EmbeddedId
	private AuthPrimarykey authPrimarykey;

	@Column(name = "cre_flag")
	private String creflag; // 新增允許
	@Column(name = "inq_flag")
	private String inqflag; // 查詢允許
	@Column(name = "upd_flag")
	private String updflag; // 修改允許
	@Column(name = "del_flag")
	private String delflag; // 刪除允許
	@Column(name = "proc_flag")
	private String procflag; // 處理允許

	public Role_Auth() {
	}

	public Role_Auth(AuthPrimarykey authPrimarykey, String creflag, String inqflag, String updflag, String delflag,
			String procflag) {
		this.authPrimarykey = authPrimarykey;
		this.creflag = creflag;
		this.inqflag = inqflag;
		this.updflag = updflag;
		this.delflag = delflag;
		this.procflag = procflag;
	}

	public AuthPrimarykey getAuthPrimarykey() {
		return authPrimarykey;
	}

	public void setAuthPrimarykey(AuthPrimarykey authPrimarykey) {
		this.authPrimarykey = authPrimarykey;
	}

	public String getCreflag() {
		return creflag;
	}

	public void setCreflag(String creflag) {
		this.creflag = creflag;
	}

	public String getInqflag() {
		return inqflag;
	}

	public void setInqflag(String inqflag) {
		this.inqflag = inqflag;
	}

	public String getUpdflag() {
		return updflag;
	}

	public void setUpdflag(String updflag) {
		this.updflag = updflag;
	}

	public String getDelflag() {
		return delflag;
	}

	public void setDelflag(String delflag) {
		this.delflag = delflag;
	}

	public String getProcflag() {
		return procflag;
	}

	public void setProcflag(String procflag) {
		this.procflag = procflag;
	}

	@Override
	public String toString() {
		return "Role_Auth [authPrimarykey=" + authPrimarykey.toString() + ", creflag=" + creflag + ", inqflag="
				+ inqflag + ", updflag=" + updflag + ", delflag=" + delflag + ", procflag=" + procflag + "]";
	}

}
