package com.infotran.springboot.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 使用者訊息表
 * @param String username - 使用者帳號;
 * @param String name - 使用者姓名;
 * @param String password - 密碼;
 * @param String deptId - 部門代號;
 * @param String department - 部門名稱;
 * @param String email - 使用者信箱;
 */
 


@Entity
@Table(name = "EmpUser")
public class UserInfo implements Serializable, UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "emp_id")
	@Type(type = "string")
	private String username;
	private String name;
	private String password;

	@Column(name = "dept_id")
	private String deptId;
	private String department;
	private String email;

	// user_role多對多關聯
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "Emp_user_role", joinColumns = { @JoinColumn(name = "emp_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	@JsonIgnore
	private List<Role> roleList;

	public UserInfo() {
	}

	public UserInfo(String username, String name, String password, String deptId, String department, String email) {
		this.username = username;
		this.name = name;
		this.password = password;
		this.deptId = deptId;
		this.department = department;
		this.email = email;
	}

	public UserInfo(String username, String name, String password, String deptId, String department, String email,
			List<Role> roleList) {
		this.username = username;
		this.name = name;
		this.password = password;
		this.deptId = deptId;
		this.department = department;
		this.email = email;
		this.roleList = roleList;
	}

	// 帳號
	// 註: username = empid
	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * 得到用戶的權限，若權限表和用戶是分開的，我們須再定義一個Entity實現UserDetails並繼承於User類別
	 * 交給security的權限，放在UserDetailService進行處理
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();

		for (Role role : roleList) {
			authorities.add(new SimpleGrantedAuthority(role.getRoleId()));
		}
		System.out.println("authorities for roles = " + authorities);
		return authorities;
	}

	/**
	 * 帳戶是否未過期 true:是 false:否
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * 帳戶是否未鎖住，無法對鎖定的用戶進行身分驗證 true:是 false:否
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * 憑證是否尚未過期，過期的憑證會阻止身分驗證 true:是 false:否
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * 指示用戶是否啟用還是禁用 true: 啟用 false: 禁用
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return "UserInfo [username=" + username + ", name=" + name + ", deptId=" + deptId + ", department=" + department
				+ ", email=" + email + "]";
	}

}
