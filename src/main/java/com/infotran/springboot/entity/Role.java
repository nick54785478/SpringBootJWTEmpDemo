package com.infotran.springboot.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * 角色表
 * @param String roleId - 角色名稱
 * @param String roleName - 角色中文名稱
 * */


@Entity
@Table(name = "EmpRole")
public class Role {

	@Id
	@Column(name = "role_id")
	@Type(type = "string")
	private String roleId;
	@Column(name = "role_name")
	private String roleName;

	@ManyToMany(mappedBy = "roleList")
	private List<UserInfo> userList;

	public Role() {

	}

	public Role(String roleId, String roleName) {
		this.roleId = roleId;
		this.roleName = roleName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<UserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfo> userList) {
		this.userList = userList;
	}

	@Override
	public String toString() {
		return "Role [roleId=" + roleId + ", roleName=" + roleName + ", userList=" + userList + "]";
	}

}
