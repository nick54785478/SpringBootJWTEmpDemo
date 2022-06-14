package com.infotran.springboot.service.Impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.infotran.springboot.entity.Role;
import com.infotran.springboot.entity.User;
import com.infotran.springboot.entity.UserInfo;
import com.infotran.springboot.repository.FuncRepository;
import com.infotran.springboot.repository.RoleRepository;
import com.infotran.springboot.repository.UserRepository;
import com.infotran.springboot.service.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private FuncRepository funcRepository;
	private static String ROLE_PREFIX = "ROLE_";

	private static final Logger log = LoggerFactory.getLogger(UserRoleServiceImpl.class);

	@Autowired
	public UserRoleServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, FuncRepository funcRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.funcRepository = funcRepository;
	}

	/**
	 * 依名稱取得使用者資料
	 */
	@Override
	public UserInfo getUserInfoByName(String username) {
		return userRepository.findByUsername(username);
	}

	/**
	 * 註冊一名使用者
	 * 
	 * @param UserInfo user - 使用者資訊
	 */
	@Override
	public UserInfo addUser(UserInfo user) {
		user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密碼
		userRepository.save(user);
		return user;
	}


	/**
	 * 依名稱取得該用戶的角色
	 * 
	 * @param String username - 使用者名稱
	 * @return List<Role> roleList - 角色資料
	 */
	@Override
	public List<Role> getRolesByName(String username) {
		List<Role> roleList = roleRepository.qryRolesByUsername(username);
		return roleList;
	}

	/**
	 * 取出所有使用者
	 */
	@Override
	public List<UserInfo> getAll() {
		return userRepository.findAll();
	}

	/**
	 * 取出所有角色
	 */
	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	/**
	 * 新增一名角色
	 */
	@Override
	public void addRole(Role role) {
		// 加上前綴
		String hasROLE = ROLE_PREFIX + role.getRoleId();
		role.setRoleId(hasROLE);
		roleRepository.save(role);
	}


	/**
	 * 匯入測試使用者資料
	 */
	@Override
	public void importUserList(List<UserInfo> userList) {
		userRepository.saveAll(userList);
	}

	/**
	 * 匯入測試角色資料
	 */
	@Override
	public void importRoleList(List<Role> roleList) {
		roleList.forEach(roleBean -> roleBean.setRoleId(ROLE_PREFIX + roleBean.getRoleId()));
		roleRepository.saveAll(roleList);
	}

	/**
	 * 修改密碼
	 */
	@Override
	public void updPassWord(User user) {
		UserInfo userInfo = userRepository.findByUsername(user.getUsername());
		userInfo.setPassword(passwordEncoder.encode(user.getNewpassword())); // 設置加密後的新密碼
		System.out.println("user.getPassword()" + user.getPassword());
		userRepository.save(userInfo);
	}

	/**
	 * 依部門去查詢用戶
	 */
	@Override
	public List<UserInfo> getAllByDepId(String deptId) {
		return userRepository.findByDeptId(deptId);
	}



	/**
	 * 刪除使用者帳號(用於使用者忘記密碼)
	 */
	@Override
	public void delUser(UserInfo userInfo) {
		userRepository.delete(userInfo);

	}

	/**
	 * 以RoleId 找出 Authority
	 * */
	@Override
	public List<Role> getAuthorityByRoleId(String roleId) {
		return roleRepository.findRoleListByRoleId(roleId);
	}

	/**
	 * 取出特定角色的資訊
	 * */
	@Override
	public Role getRoleByRoleId(String roleId) {
		return roleRepository.findByRoleId(roleId);
	}

	/**
	 * 修改使用者資訊
	 * */
	@Override
	public void updUserInfo(UserInfo userInfo) {
		userRepository.save(userInfo);
		
	}

	/***/
	@Override
	public List<Role> getAllRolesBySrvltPath(String servletPath) {
		Boolean isPermitAll = "Y".equals(funcRepository.findByServletPath(servletPath).getPermitAll()) ? true : false;
		if (isPermitAll) {
			return roleRepository.findAll();
		}

		return roleRepository.qryRoleBySrvltPath(servletPath);
	}

}
