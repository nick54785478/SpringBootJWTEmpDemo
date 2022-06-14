package com.infotran.springboot.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/supervisor")
public class SupervisorController {

	private static final Logger log = LoggerFactory.getLogger(SupervisorController.class);

	private UserRoleServiceImpl urService;
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	public SupervisorController(UserRoleServiceImpl urService, JwtTokenUtil jwtTokenUtil) {
		this.urService = urService;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	/**
	 * 註冊功能:Supervisor 才能使用。
	 * 
	 * @param {JavaBean} User - 使用者資訊 User(username, password, departmentid,
	 *                   department, role, newpassword)
	 */
	@PostMapping("/register")
	public ResponseEntity<Object> addNewUser(@RequestBody User user, HttpServletRequest request) {

		// 從SecurityContextHolder獲取當前用戶信息
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, String> details = (Map<String, String>) authentication.getDetails();
		String role = details.get(jwtTokenUtil.ROLE_CLAIMS);
		String deptId = details.get(jwtTokenUtil.DEPTID);

		// 從Token中讀取相關資訊
//         	String header = request.getHeader(jwtTokenUtil.TOKEN_HEADER);
//         	String tokenBody = header.replace(jwtTokenUtil.TOKEN_PREFIX, ""); 
//         	String role = jwtTokenUtil.getUserRole(tokenBody);
		log.info("Role:" + role + ", DeptId:" + deptId);

		// 角色表
		List<Role> roleList = urService.getAuthorityByRoleId(user.getRole());
		UserInfo userInfo = new UserInfo(user.getUsername(), user.getName(), user.getPassword(), user.getDeptId(),
				user.getDepartment(), user.getEmail(), roleList);

		// 不能註冊root或同級以上角色
		if (user.getRole().equals(role) || "ROLE_root".equals(user.getRole())) {
			log.error("角色為" + role + "欲註冊的角色為" + user.getRole() + " 不能註冊root或同級角色");
			throw new ExceptionMsg("00100", "用戶註冊錯誤。");
		}

		// 不同部門不能註冊
		if (!"ROLE_root".equals(role) && !user.getDeptId().equals(deptId)) {
			log.error("角色為" + role + "，所屬部門代號為" + deptId + "，欲註冊的部門代號為" + user.getDeptId() + ", 部門別不同");
			throw new ExceptionMsg("00100", "用戶註冊錯誤。");
		}

		// 新增使用者資訊 UserInfo
		urService.addUser(userInfo);
	
		log.info("新增使用者資訊: \n 使用者:" + user.getUsername() + "， 角色:" + user.getRole());

		return new ResponseEntity<>("成功新增一名使用者: \n" + userInfo.toString(), HttpStatus.OK);
	}

}
