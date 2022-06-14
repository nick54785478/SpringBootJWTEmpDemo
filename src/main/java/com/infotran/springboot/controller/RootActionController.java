package com.infotran.springboot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infotran.springboot.entity.Func;
import com.infotran.springboot.entity.Role;
import com.infotran.springboot.entity.Role_Auth;
import com.infotran.springboot.entity.User;
import com.infotran.springboot.entity.UserInfo;
import com.infotran.springboot.service.Impl.AuthServiceImpl;
import com.infotran.springboot.service.Impl.UserFuncServiceImpl;
import com.infotran.springboot.service.Impl.UserRoleServiceImpl;


@RestController
@RequestMapping("/root")
public class RootActionController {

	private static final Logger log = LoggerFactory.getLogger(RootActionController.class);

	
	private UserRoleServiceImpl urService;
	private UserFuncServiceImpl ufService;
	private AuthServiceImpl authService;

	@Autowired
	public RootActionController(UserRoleServiceImpl urService, UserFuncServiceImpl ufService, 
			AuthServiceImpl authService) {
		this.urService = urService;
		this.ufService = ufService;
		this.authService = authService;
	}
	
	/**
	 * 註冊帳號(可註冊管理者)
	 * */ 
	@PostMapping("/register")
	public ResponseEntity<Object> addAdmin(@RequestBody User user) {		
		
		// 取出角色表
		List<Role> roleList = urService.getAuthorityByRoleId(user.getRole());
		
		// 新增使用者資訊
		UserInfo userInfo = new UserInfo(user.getUsername(), user.getName(), user.getPassword(),
				user.getDeptId(), user.getDepartment(), user.getEmail(), roleList);
		urService.addUser(userInfo);		
		
		return new ResponseEntity<>("成功新增一名使用者: \n"+userInfo.toString(), HttpStatus.OK);
	}

	/**
	 * 取出所有角色資訊
	 * */ 
	@GetMapping("/getAllRoles")
	public List<Role> findAllRoles() {
		return urService.getAllRoles();
	}

	/**
	 * 取出所有權限
	 * */
	@GetMapping("/getAllFuncs")
	public List<Func> findAllFuncs(){
		return ufService.getAllFunc();
	}

	/**
	 * 取出所有角色權限
	 * */
	@GetMapping("/getAllAuths")
	public List<Role_Auth> findAllAuths(){
		return authService.getAllRoleAuth();
	}
	
	
	
	/**
	 * 新增角色資料
	 * @param {List<Role>} roleList - 存放 Role 的 List
	 * Role(rolename, nameZh)
	 * */ 
	@PostMapping("/importRoleData")
	public ResponseEntity<Object> importRoleData(@RequestBody List<Role> roleList) {
		urService.importRoleList(roleList);
		log.info("成功新增多筆角色資料: \n"+roleList);
		return new ResponseEntity<>("成功新增多筆角色資料 ", HttpStatus.OK);
	}
	
	/**
	 * 新增使用者資料
	 * @param {List<User>} userList - 存放 User 的 List
	 * */ 
	@PostMapping("/importUserData")
	public ResponseEntity<Object> importUserData(@RequestBody List<User> beans) {

		for (User bean : beans) {
			List<Role> roleList = urService.getAuthorityByRoleId(bean.getRole()); 
			UserInfo userInfo = new UserInfo(bean.getUsername(), bean.getName(), bean.getPassword(),
					bean.getDeptId(), bean.getDepartment(), bean.getEmail(), roleList);
			urService.addUser(userInfo);  // 插入用戶資料					
			log.info("成功新增一筆資料: username:"+userInfo.getUsername()+", name:"+userInfo.getName()+
					", deptId:"+userInfo.getDeptId()+", email:"+userInfo.getEmail());
		
			
		}				
		return new ResponseEntity<>("成功新增多筆使用者資料", HttpStatus.OK);
	}
	
	/**
	 * 新增權限功能資料
	 * @param: {List<Func>} FuncList
	 * */
	@PostMapping("/importAuthorityData")
	public ResponseEntity<Object> importAuthorityData(@RequestBody List<Func> beans) {
		beans.forEach(func -> System.out.println("Func = "+func));
		ufService.importNewFuncList(beans);
		log.info("成功新增多筆權限功能資料: "+beans);
		
		return new ResponseEntity<>("成功新增權限功能資料", HttpStatus.OK);
	}
	
	
	
	/**
	 * 新增權限控制資料
	 * @param: {List<Role_Auth>} RoleAuthList
	 * */
	@PostMapping("/importRoleAuthData")
	public ResponseEntity<Object> importRoleAuthData(@RequestBody List<Role_Auth> beans) {
		authService.importRoleAuthList(beans);
		return new ResponseEntity<>("成功新增權限控制資料", HttpStatus.OK);
	}
	
	/**
	 * 取出所有權限功能資料
	 * @return {List<Func>} FuncList 
	 * */
	@GetMapping("/getAllAuthority")
	public List<Func> getAllAuthorities() {
		log.info("取出所有權限功能資料: "+ufService.getAllFunc());
		return ufService.getAllFunc();
	}
	
	
	/**
	 * 查詢能使用該功能的角色
	 * */
	@GetMapping("/getAllRolesBySrvltPath")
	public ResponseEntity<Object> getAllRolesByServletPath(@RequestParam String servletPath){
		List<Role> roles = urService.getAllRolesBySrvltPath(servletPath);
		if ("Y".equals(ufService.getBySrvltPath(servletPath).getPermitAll())) {
			roles = urService.getAllRoles();
		}
		
		return new ResponseEntity<>(roles, HttpStatus.OK);
	}
	

	
	/**
	 * 查詢該角色能進入的ServletPath
	 * */
	@GetMapping("/getAllSrvltPathsByRole")
	public ResponseEntity<Object> getAllFuncsByRole(@RequestParam String roleId) {
		List<String> srvltpaths = ufService.getAllSrvltPathByRole("ROLE_"+roleId);
		log.info("srvltpaths = "+srvltpaths);
		List<String> permitAllPath = ufService.getPermitAll();
		srvltpaths.addAll(permitAllPath);
		log.info("permitAllPath = "+permitAllPath);
		return new ResponseEntity<>(srvltpaths, HttpStatus.OK);
	}



 



}

