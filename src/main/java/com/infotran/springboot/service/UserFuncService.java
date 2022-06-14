package com.infotran.springboot.service;
 
import java.util.List;
 
import com.infotran.springboot.entity.Func;
 
public interface UserFuncService {
	
	void importNewFuncList(List<Func> funcList); // 匯入多筆權限功能資料
 
	Func getBySrvltPath(String servletPath); // 用servletPath查詢Func

	List<String> getAuthoritiesBySrvltPath(String servletPath);  // 藉由 ServletPath 搜尋角色
    	
    List<Func> getAllFunc(); // 查詢所有權限功能資料

    List<String> getPermitAll(); // 查詢允許所有人存取的ServletPath
    
    List<String> getAllSrvltPath();  // 查詢所有ServletPath
    	
    List<String> getAllSrvltPathByRole(String roleId); // 查詢該角色能進入的ServletPath
    	
}
