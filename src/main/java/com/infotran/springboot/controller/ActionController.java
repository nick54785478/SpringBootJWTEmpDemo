package com.infotran.springboot.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infotran.springboot.entity.User;
import com.infotran.springboot.entity.UserInfo;
import com.infotran.springboot.service.Impl.AuthServiceImpl;
import com.infotran.springboot.service.Impl.UserFuncServiceImpl;
import com.infotran.springboot.service.Impl.UserRoleServiceImpl;
import com.infotran.springboot.util.ExceptionMsg;
import com.infotran.springboot.util.JwtTokenUtil;

//使用者角色控制器
@RestController
public class ActionController {

	private static final Logger log = LoggerFactory.getLogger(ActionController.class);

	
	private UserRoleServiceImpl urService;	
	private UserFuncServiceImpl ufService;
	private PasswordEncoder passwordEncoder;
	private JwtTokenUtil jwtTokenUtil;
	private AuthServiceImpl  authService;
	
	
	@Autowired
	public ActionController(UserRoleServiceImpl urService, UserFuncServiceImpl ufService,
			PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, AuthServiceImpl authService) {
		this.urService = urService;
		this.ufService = ufService;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenUtil = jwtTokenUtil;
		this.authService = authService;
	}

	/**
	 * 取得單一用戶的資訊
	 */
	@GetMapping("/getUserByName")
	public ResponseEntity<UserInfo> getUserByName(@RequestParam String username, HttpSession session,
			HttpServletRequest request) {
		UserInfo userInfo = urService.getUserInfoByName(username);
		// 此人不存在
		if (userInfo == null) {
			log.error("查無此用戶");
			throw new UsernameNotFoundException("查無此用戶");
		}

		return new ResponseEntity<UserInfo>(userInfo, HttpStatus.OK);

	}

	/**
	 * 取出所有人員資訊:
	 * 
	 */
	@GetMapping("/getAll")
	public List<UserInfo> findAll(HttpServletRequest request) {
		// 從SecurityContextHolder獲取當前用戶信息
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, String> details = (Map<String, String>) authentication.getDetails();
		String deptId = details.get(jwtTokenUtil.DEPTID);
		String role = details.get(jwtTokenUtil.ROLE_CLAIMS);

		// 從Token中讀取相關資訊
//         	String header = request.getHeader(jwtTokenUtil.TOKEN_HEADER);
//         	String tokenBody = header.replace(jwtTokenUtil.TOKEN_PREFIX, ""); 
//         	String deptId = jwtTokenUtil.getDeptId(tokenBody);
//         	List<UserInfo> beans = urService.findAllByDepId(deptId);                	    	
		return urService.getAll();
	}

	/**
	 * 查詢自己的角色:
	 */
	@GetMapping("/getMyRole")
	public Collection<? extends GrantedAuthority> getAuthority(HttpServletRequest request) {
		// 從SecurityContextHolder獲取當前用戶信息
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserInfo userInfo = urService.getUserInfoByName((String) authentication.getPrincipal());
		Collection<? extends GrantedAuthority> authorities = userInfo.getAuthorities();

		return authorities;
	}

	/**
	 * 修改密碼(自己只能改自己的)
	 */
	@PutMapping("/changePassword")
	public String changePWD(@RequestBody User user, Principal principal, HttpServletRequest request) {
		log.info("舊密碼: " + user.getPassword() + "   新密碼: " + user.getNewpassword());
		UserInfo updateBean = urService.getUserInfoByName(user.getUsername());
		log.info("updateBean = " + updateBean);

		// 查無此人。
		if (updateBean == null) {
			log.error("查無此用戶");
			throw new UsernameNotFoundException("用戶不存在");
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, String> details = (Map<String, String>) authentication.getDetails();

		if (!(principal.getName().equals(updateBean.getUsername()))) {
			log.error("不能修改其他帳號的密碼");
			throw new AccessDeniedException("不能修改其他帳號的密碼");
		}

		// 若舊密碼輸入正確 => 更改成功
		if (passwordEncoder.matches(user.getPassword(), updateBean.getPassword())) {
			urService.updPassWord(user);
			return "Update OK";
		}
		log.error("密碼輸入錯誤，修改失敗!");
		throw new ExceptionMsg("00104", "密碼不一致，修改失敗!");

	}

	/**
	 * 查詢自己可以進入的ServletPath
	 */
	@GetMapping("/getMyPermission")
	public ResponseEntity<Object> getMyPermission(HttpServletRequest request) {
		String header = request.getHeader(jwtTokenUtil.TOKEN_HEADER);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, String> details = (Map<String, String>) authentication.getDetails();
		String role = details.get(jwtTokenUtil.ROLE_CLAIMS); // 取出角色身分及其所對應的權限
		List<String> srvltpaths;
		
		if ("ROLE_root".equals(role)) {
			srvltpaths = ufService.getAllSrvltPath();
		} else {
			srvltpaths = ufService.getAllSrvltPathByRole(role);
			List<String> permitAllPath = ufService.getPermitAll();  // 所有為permitAll的ServletPath
			srvltpaths.addAll(permitAllPath);			
		}
		
		return new ResponseEntity<>(srvltpaths, HttpStatus.OK);
	}

	/**
	 * 解析Token
	 */
	@PostMapping(value = "/parse", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> parseToken(@RequestBody Map<String, String> request) {
		String token = request.get("token");
		Map<String, Object> response = JwtTokenUtil.parseToken(token);

		String deptId = JwtTokenUtil.getDeptId(token);
		System.out.println("deptId: " + deptId);

		return ResponseEntity.ok(response);

	}

	// 測試Session是否還在(帳號是否登出)
	@GetMapping("/getSessionName")
	public String printSessionName(HttpSession session) {

		if (session.getAttribute("username") != null) {
			System.out.println("username: " + session.getAttribute("username"));

			return "Session尚未刪除，帳號尚未登出";
		}

		return "Session已刪除，帳號登出";
	}
}
