package com.infotran.springboot.config;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.infotran.springboot.module.filter.AuthorityFilter;
import com.infotran.springboot.module.filter.JWTAuthenticationFilter;
import com.infotran.springboot.module.handler.DeniedHandler;
import com.infotran.springboot.module.handler.LoginFailHandler;
import com.infotran.springboot.module.handler.LoginSuccessHandler;
import com.infotran.springboot.service.Impl.CustomUserDetailsService;
import com.infotran.springboot.util.AuthDetailsSource;
 
@Configuration
/**
 * EnableGlobalMethodSecurity:  開啟方法及安全驗證
 *   prePostEnabled=true會解鎖 @PreAuthorize 和 @PostAuthorize 兩註解
 *  @PreAuthorize 會在方法執行前驗證
 *  @PostAuthorize 會在方法執行後驗證
 * */
@EnableWebSecurity  //用於啟用Web安全的註解
@EnableGlobalMethodSecurity(prePostEnabled = true) 
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
    	@Autowired
    	private CustomUserDetailsService cudService;
 
    	@Autowired
    	private LoginSuccessHandler successHandler;
    	@Autowired
    	private LoginFailHandler failHandler;
    	@Autowired
    	private DeniedHandler accessDeniedHandler;
    	@Autowired
    	private AuthDetailsSource authDetailsSource;
 
    	
 
    	/**
    	 * 認證管理器
    	 * */
    	@Bean
    	@Override
    	protected AuthenticationManager authenticationManager() throws Exception {
            	return super.authenticationManager();
    	}
    	
    	/**
    	 *
    	 * */
    	@Bean
    	UserDetailsService CustomUserDetailsService() {
            	return new CustomUserDetailsService();
    	}
    	
    	/**
    	 * 註冊AuthorityFilter
    	 * */
    	@Bean
    	AuthorityFilter authorityFilter() {
            	return new AuthorityFilter();
    	}
    	
    	/**
    	 * 指定加密方式
    	 * */
    	@Bean
    	public PasswordEncoder passwordEncoder() {
            	//使用BCrypt加密
            	return new BCryptPasswordEncoder();
    	}
 
    	
    	@Override
    	public void configure(WebSecurity web) throws Exception {
            	web.ignoring().antMatchers("/static/**"); //不攔截靜態資源
            	web.ignoring().antMatchers("/templates/**");
            	web.ignoring().antMatchers("/h2-console/**");  // 不攔截h2-console
    	}
    	
    	@Override
    	protected void configure(AuthenticationManagerBuilder auth) throws Exception {	            	
            	//從資料庫讀取的用戶進行身份認證
            	auth.userDetailsService(cudService).passwordEncoder(passwordEncoder());
            	
    	}
            	
    	/**
    	 * API授權規則:
    	 *   permitAll: 允許所有請求
    	 *   authenticated: 通過身分驗證即可存取
    	 * */
    	@Override
    	protected void configure(HttpSecurity http) throws Exception {
            	
            	// post請求要關閉csrf(對跨站請求偽造攻擊的防護)驗證，不然會訪問錯誤；實際開發中開啟，須前端配合傳遞其他參數
            	http.cors().and().csrf().disable()
                    	.authorizeRequests()
                    	.antMatchers("/","/index", "/login", "/css/**", "/js/**").permitAll()  //允許所有呼叫方存取，不用權限即可進入
                    	.antMatchers(HttpMethod.GET, "/getSessionName").permitAll()  // 測試用，測試Session是否存在
                    	.anyRequest().authenticated()	// 剩下所有請求都需身份驗證
                    	.and()
                    	.formLogin()
                    	.loginPage("/login")  //自定義登入頁面
                    	.failureUrl("/login?error") // 失敗路徑
                    	.usernameParameter("username")   //自定義接收前端用戶名欄位
                    	.passwordParameter("password")  //自定義接收前端密碼欄位
                    	.defaultSuccessUrl("/home")     	// 預設登入成功頁面
                    	.successHandler(successHandler)
                    	.failureHandler(failHandler)
                    	.authenticationDetailsSource(authDetailsSource)
                    	.and()
                    	.addFilter(new JWTAuthenticationFilter(authenticationManager()))
                    	;
            	
            	//session 管理
//         	http.sessionManagement()
//         	.invalidSessionUrl("/session/invalid");//Session過期跳轉路徑
//         	.sessionAuthenticationFailureHandler(failHandler)
//         	.invalidSessionStrategy(customInvalidSessionStrategy)
            	
 
            	// 記得我
            	http.rememberMe().rememberMeParameter("rememberme")
                    	.tokenValiditySeconds(200)
            	// 指定 UserDetailsService 對象
            .userDetailsService(CustomUserDetailsService());;
            	
            	
            	// 登出  
            	http.logout().permitAll().deleteCookies("rememberme");
    	
            	// 例外處理，拒絕造訪就重導向403頁面
            	http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    	
    	}
    	
}
