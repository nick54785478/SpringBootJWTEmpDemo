package com.infotran.springboot.service;
 
import java.util.List;

import com.infotran.springboot.entity.Role;
import com.infotran.springboot.entity.User;
import com.infotran.springboot.entity.UserInfo;
 
public interface UserRoleService {
	
    	
	void importUserList(List<UserInfo> userList); // 插入多筆使用者資料
	
	void importRoleList(List<Role> roleList); // 插入多筆角色資料
	
	void addRole(Role role);     	// 新增一筆角色

	UserInfo getUserInfoByName(String name);  // 依使用者帳號取得資料
 
	UserInfo addUser(UserInfo user);  // 新增一筆資料
    	
    Role getRoleByRoleId(String roleId); // 取出角色資訊
    	
    List<Role> getRolesByName(String username); // 取得符合名稱的角色們
    	
    List<UserInfo> getAll();   // 發現所有用戶資訊(依目前登入者)
            	
    List<Role> getAllRoles(); // 取出所有角色
    	
    List<Role> getAllRolesBySrvltPath(String servletPath); // 查詢能使用該功能的所有角色

    List<UserInfo> getAllByDepId(String deptId);  // 依部門取出所有人

    List<Role> getAuthorityByRoleId(String roleId); // 以RoleId找出Authority

    void updUserInfo(UserInfo userInfo); // 修改使用者資料
    	
    void updPassWord(User user);  //修改密碼
    	
    void delUser(UserInfo userInfo);
 
    	
}
