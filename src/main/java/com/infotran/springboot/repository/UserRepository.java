package com.infotran.springboot.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infotran.springboot.entity.UserInfo;

/**
 * 使用的JavaBean: UserInfo
 * @param String emp_id - 使用者帳號;
 * @param String name - 使用者名稱;
 * @param String password - 密碼;
 * @param String dept_id - 部門代號;
 * @param String department - 部門名稱;
 * @param String email - 信箱;
 */

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {

	UserInfo findByUsername(String username); // 依帳號取得密碼

	List<UserInfo> findByDeptId(String deptId); // 依部門查詢使用者資料
	
//	// 新增一筆權限對應
//	@Transactional
//	@Modifying
//	@Query(value = "INSERT INTO Emp_user_role(emp_id, role_id) VALUES(?1, ?2)", nativeQuery = true)
//	void inertNewAuthority(String username, String role);

}
