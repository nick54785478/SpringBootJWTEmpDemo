package com.infotran.springboot.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infotran.springboot.entity.Role_Auth;
import com.infotran.springboot.repository.AuthRepository;
import com.infotran.springboot.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	
	@Autowired
	private AuthRepository authRepository;


	@Override
	public List<Role_Auth> getAllRoleAuth() {
		return authRepository.findAll();
	}

	@Override
	public void importRoleAuthList(List<Role_Auth> beans) {
		authRepository.saveAll(beans);
	}

	@Override
	public void addRoleAuth(Role_Auth role_Auth) {
		authRepository.save(role_Auth);

	}




	

}
