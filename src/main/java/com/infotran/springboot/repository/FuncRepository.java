package com.infotran.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infotran.springboot.entity.Func;

/**
 * 使用的JavaBean: Func
 *	@param String func_id - 功能代號;
 *	@param String func_type - 功能分類(S:系統/B:業務);
 * 	@param String func_name - 功能名稱; 
 * 	@param String servlet_path - 對應網址;
 *	@param String disp_order - 顯示順序;
 * */
@Repository
public interface FuncRepository extends JpaRepository<Func, String> {

	/**
	 * 使用ServletPath搜尋符合條件Func
	 * @param String servletPath - ServletPath路徑
	 * @return Func - 符合相關路徑的JavaBean
	 * */
	Func findByServletPath(String servletPath);  
	
	// 查詢該角色能訪問的ServletPath
	@Query(value = "SELECT servlet_path FROM EmpFunc WHERE func_id IN"
					+ " (SELECT func_id FROM Emp_role_auth WHERE role_id = ?1)", nativeQuery = true)
	List<String> qrySrvltPathByRole(String roleId);
	
	// 查詢所有的ServletPath
	@Query(value = "SELECT servlet_path FROM EmpFunc WHERE func_id IN"
						+ " (SELECT func_id FROM Emp_role_auth)", nativeQuery = true)
	List<String> qryAllSrvltPath();
	
	// 查詢允許所有人存取的ServletPath
	@Query(value = "SELECT servlet_path FROM EmpFunc WHERE permit_all = 'Y'", nativeQuery = true)
	List<String> qryPermitAllSrvletPath();



}

