package com.infotran.springboot.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infotran.springboot.entity.Func;
import com.infotran.springboot.repository.FuncRepository;
import com.infotran.springboot.repository.RoleRepository;
import com.infotran.springboot.service.UserFuncService;

@Service
public class UserFuncServiceImpl implements UserFuncService {

	private FuncRepository funcRepository;
	private RoleRepository roleRepository;
	
	@Autowired
	public UserFuncServiceImpl(FuncRepository funcRepository,
			RoleRepository roleRepository) {
		this.funcRepository = funcRepository;
		this.roleRepository = roleRepository;
	}



	/**
	 * 利用ServletPath找出可以進入的角色
	 * func{funcId} -> role_auth{funcId} -> role_auth{roleId}
	 * @param String url - 通常為ServletPath
	 * @return List<String> roles - 可進入ServletPath的角色
	 * */
	@Override
	public List<String> getAuthoritiesBySrvltPath(String servletPath) {
		List<String> roles = roleRepository.qryRolesBySrvltPath(servletPath);
		return roles;
	}


	/**
	 * 新增多筆資料 
	 * @param {List<Func>} - 多筆Func資料
	 * */
	@Override
	public void importNewFuncList(List<Func> funcList) {
		funcRepository.saveAll(funcList);
	}


	/**
	 * 取出所有權限功能資料
	 * @return {List<Func>} - 權限功能資料  
	 * */
	@Override
	public List<Func> getAllFunc() {
		return funcRepository.findAll();
	}


	/**
	 * 取得單筆權限功能
	 * @param String url - ServletPath
	 * @return Func func - 單筆權限功能資料 
	 * */
	@Override
	public Func getBySrvltPath(String url) {
		return funcRepository.findByServletPath(url);
	}


	/**
	 * 取出所有為 permitAll 的ServletPath
	 * */
	@Override
	public List<String> getPermitAll() {
		return funcRepository.qryPermitAllSrvletPath();
	}


	/**
	 * 取出所有ServletPath
	 * @return List<String> - 所有ServletPath
	 * */
	@Override
	public List<String> getAllSrvltPath() {
		return funcRepository.qryAllSrvltPath();
	}


	/**
	 * 取出該角色能進入的所有ServletPath
	 * @return List<String> - 所有符合該角色的ServletPath
	 * */
	@Override
	public List<String> getAllSrvltPathByRole(String roleId) {
		return funcRepository.qrySrvltPathByRole(roleId);
	}
}
