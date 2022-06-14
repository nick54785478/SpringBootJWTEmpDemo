package com.infotran.springboot.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.infotran.springboot.entity.UserInfo;

/**
 * 進行身份認證 要從DB讀取用戶資訊進行身分認證，需繼承 UserDetailsService，並改寫loadUserByUsername方法，
 * 該方法的參數為登入時的用戶名，通過該用戶名去資料庫查找用戶，若不存在則拋出用戶不存在的異常，反之，返回用戶及角色訊息， 此方法在用戶登入時自動調用。
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

	@Autowired
	private UserRoleServiceImpl urService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 透過用戶名從資料庫獲取訊息
		UserInfo userInfo = urService.getUserInfoByName(username);

		if (userInfo == null) {
			log.error("用戶不存在");
			throw new UsernameNotFoundException("用戶不存在");
		}

		System.out.println("userInfo.getUsername() = " + userInfo.getUsername()); // root
		userInfo.setRoleList(urService.getRolesByName(userInfo.getUsername()));
		log.info("登入使用者資訊: " + userInfo.toString());
		return userInfo;

	}

}
