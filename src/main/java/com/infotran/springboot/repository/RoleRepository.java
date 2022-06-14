package com.infotran.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infotran.springboot.entity.Role;

/**
 * 使用的JavaBean: Role
 * @param String         role_id - 角色代號;
 * @param String         role_name - 角色名稱;
 * @param List<UserInfo> userList - 使用者資訊列表;
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

	Role findByRoleId(String roleId);
	
	List<Role> findRoleListByRoleId(String roleId);
	
	// 使用子查詢跨表查使用者資料
	@Query(value = "select * from EmpRole where role_id in "
				+ "(select role_id from Emp_user_role where emp_id = ?1)", nativeQuery = true)
	List<Role> qryRolesByUsername(String username);
	
	
	// 使用子查詢跨表查詢可進入某servletPath的角色
	@Query(value = "select role_id FROM Emp_role_auth " + "where func_id  in"
				+ " (select func_id from EmpFunc where servlet_path = ?1)", nativeQuery = true)
	List<String> qryRolesBySrvltPath(String servletPath);


	// 查詢能使用該功能的角色
	@Query(value = "SELECT * FROM EmpRole WHERE role_id IN " + "(" + "SELECT Emp_role_auth.role_id "
				+ "	FROM Emp_role_auth INNER JOIN EmpFunc " + "	ON Emp_role_auth.func_id = EmpFunc.func_id "
				+ "	WHERE EmpFunc.servlet_path = ?1" + ")", nativeQuery = true)
	List<Role> qryRoleBySrvltPath(String servletPath);

}
