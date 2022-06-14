package com.infotran.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infotran.springboot.entity.Role;
import com.infotran.springboot.entity.User;
import com.infotran.springboot.entity.UserInfo;
import com.infotran.springboot.service.Impl.UserRoleServiceImpl;
import com.infotran.springboot.util.ExceptionMsg;
import com.infotran.springboot.util.JwtTokenUtil;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private static final Logger log = LoggerFactory.getLogger(AdminController.class);

	private UserRoleServiceImpl urService;
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	public AdminController(UserRoleServiceImpl urService, JwtTokenUtil jwtTokenUtil) {
		this.urService = urService;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	/**
	 * 新增角色(僅系統管理者以上權限能使用)
	 */
	@PostMapping("/addRole")
	public ResponseEntity<Object> addRole(@RequestBody Role role) {
		urService.addRole(role);
		return new ResponseEntity<>("成功新增一名角色: " + role.toString(), HttpStatus.OK);
	}

	/**
	 * 賦予特定用戶特定角色權限
	 */
	@PutMapping("/giveAuthority")
	public ResponseEntity<Object> addAuthority(@RequestBody User user) {
		UserInfo userInfo = urService.getUserInfoByName(user.getUsername());
		// 此人不存在
		if (userInfo == null) {
			log.error("查無此用戶");
			throw new UsernameNotFoundException("查無此用戶");
		}

		if ("root".equals(user.getRole())) {
			throw new AccessDeniedException("不可以賦予最高權限");
		}
		
		// 取出該用戶的角色身分
		List<Role> roleList = userInfo.getRoleList();
		
		// 加入新角色身分
		Role addRole = urService.getRoleByRoleId(user.getRole()); 
		log.info("新增加的role: "+addRole);
		roleList.add(addRole);	
		userInfo.setRoleList(roleList);
		
		urService.updUserInfo(userInfo);
		
		return new ResponseEntity<>("賦予 員工代號:" + user.getUsername() + "的用戶 " + user.getRole() + " 的權限", HttpStatus.OK);
	}

	/**
	 * 解除特定用戶的特定角色權限
	 */
	@PutMapping("/removeAuthority")
	public ResponseEntity<Object> removeAuthority(@RequestBody User user) {
		UserInfo userInfo = urService.getUserInfoByName(user.getUsername());

		// 此人不存在
		if (userInfo == null) {
			log.error("查無此用戶");
			throw new UsernameNotFoundException("查無此用戶");
		}
		
		
		List<Role> newList = new ArrayList<>();   // 新的List
		List<Role> roleList = userInfo.getRoleList(); // 取出該用戶的角色身分
		
		for (Role role : roleList) {
			
			if (!user.getRole().equals(role.getRoleId())) {
				newList.add(role);
			}
		}

		newList.forEach(r-> System.out.println("role = "+r.getRoleId()));
		userInfo.setRoleList(newList);
		urService.updUserInfo(userInfo);
		return new ResponseEntity<>("解除 員工代號:" + user.getUsername() + "的用戶 " + user.getRole() + " 的權限", HttpStatus.OK);
	}

	/**
	 * 新增一個帳號(Supervisor) 註: 從Token獲取資訊
	 */
	@PostMapping("/register")
	public ResponseEntity<Object> addSupervisor(@RequestBody User user, HttpServletRequest request) {
		
		// 從SecurityContextHolder獲取當前用戶信息
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, String> details = (Map<String, String>) authentication.getDetails();
		String role = details.get(jwtTokenUtil.ROLE_CLAIMS);
		log.info("Role = " + role);

		// 從Token中讀取相關資訊
//         	String header = request.getHeader(jwtTokenUtil.TOKEN_HEADER);
//         	String tokenBody = header.replace(jwtTokenUtil.TOKEN_PREFIX, ""); 
//         	String role = jwtTokenUtil.getUserRole(tokenBody);
//         	System.out.println("Role = "+role );

		// 不能註冊root或同級以上角色
		if (user.getRole().equals(role) || "ROLE_root".equals(user.getRole())) {
			log.error("角色為" + role + "欲註冊的角色為" + user.getRole() + " 不能註冊root或同級角色");
			throw new ExceptionMsg("00100", "用戶註冊錯誤。");
		}

		// 取出角色表
		List<Role> roleList = urService.getAuthorityByRoleId(user.getRole());
		
		// 新增使用者資訊
		UserInfo userInfo = new UserInfo(user.getUsername(), user.getName(), user.getPassword(), user.getDeptId(),
				user.getDepartment(), user.getEmail(), roleList);
		System.out.println("userInfo = "+userInfo.toString());
		urService.addUser(userInfo);

		return new ResponseEntity<>("成功新增一名使用者: \n" + userInfo.toString(), HttpStatus.OK);
	}

	
	@DeleteMapping("/deleteUser")
	public ResponseEntity<Object> delUser(@RequestBody UserInfo userInfo) {
		if ("root".equals(userInfo.getUsername())) {
			throw new AccessDeniedException("不可刪除root帳號");
		}
		urService.delUser(userInfo);
		return new ResponseEntity<>("成功刪除一名使用者", HttpStatus.OK);
	}

}
