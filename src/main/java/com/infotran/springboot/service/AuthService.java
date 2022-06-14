package com.infotran.springboot.service;

import java.util.List;

import com.infotran.springboot.entity.Role_Auth;

public interface AuthService {

	// 找出所有角色權限
	List<Role_Auth> getAllRoleAuth();

	// 插入角色權限控制資料
	void addRoleAuth(Role_Auth role_Auth);

	// 匯入權限控制資料
	void importRoleAuthList(List<Role_Auth> role_Auths);


	

	

}
