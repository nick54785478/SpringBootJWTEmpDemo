package com.infotran.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infotran.springboot.entity.AuthPrimarykey;
import com.infotran.springboot.entity.Role_Auth;

/**
 * 讀取的JavaBean為Role_Auth
 * @param AuthPrimarykey authPrimarykey - 複合主鍵(dept_id:部門代號, role_id:角色代號, func_id:功能代號)
 * @param String         cre_flag - 新增允許;
 * @param String         inq_flag - 查詢允許;
 * @param String         upd_flag - 修改允許 ;
 * @param String         del_flag - 刪除允許;
 * @param String         proc_flag - 處理允許;
 */
@Repository
public interface AuthRepository extends JpaRepository<Role_Auth, AuthPrimarykey> {

	List<Role_Auth> findByAuthPrimarykey(AuthPrimarykey authPrimarykey); // 使用複合主鍵搜尋

	List<Role_Auth> findAll(); // 查詢所有
	
	

}
