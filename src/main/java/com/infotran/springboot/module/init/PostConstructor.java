package com.infotran.springboot.module.init;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.infotran.springboot.entity.Role;
import com.infotran.springboot.entity.UserInfo;
import com.infotran.springboot.service.Impl.UserRoleServiceImpl;

/**
 * 初始化設定: 
 * 	o 使用註解: @PostConstruct 
 * 	o 效果: 在Bean初始化之後(構造方法和@autowired之後)執行指定操作。常在將構造方法中的動作延遲
 * 	o 註: Bean初始化的執行順序: 構造方法 -> @Autowired -> @PostConstruct
 */

@Component
public class PostConstructor {

	private static final Role ROLE = new Role("root", "超級管理員");
	private static final String ROOT = "ROLE_root";
	private static final Logger log = LoggerFactory.getLogger(PostConstructor.class);

	@Autowired
	UserRoleServiceImpl urService;

	public PostConstructor() {

	}

	// 初始化方法
	@PostConstruct
	public void init() {
		UserInfo root = urService.getUserInfoByName("root");
		log.info("root bean: " + root);
		// 若root不存在於資料庫，建立一個
		if (root == null) {
			log.info("資料庫為空，插入一筆帳號root");
			// 角色表
			ArrayList<Role> roleList = new ArrayList<Role>();
			roleList.add(ROLE);
			
			urService.addRole(ROLE);
			root = new UserInfo("root", "root", "123", "300", "資訊部", "root@gmail.com", roleList);
			urService.addUser(root);
			log.info("成功新增一個帳號root: " + root);
		}



		// 權限表
//		urService.addAuthority(new Authority("root", "ROLE_root"));

		log.info("##############--- 成功初始化 ---#############");

	}

}
