package com.infotran.springboot.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	private final static String SIGNATURE_WORD = "AbcDefGhiJklMnoPqrStuVwxYz_"; // 秘鑰

	private static final String ISS = "root"; // 簽發者

	public static final String DEPTID = "deptId";

	private static final long EXPIRATION = 3600L; // 過期時間若是3600L秒，即1個小時

	public static final String ROLE_CLAIMS = "role"; // 角色的key

	/**
	 * 建立token 參數:
	 * 
	 * @param String username - 使用者名稱
	 * @param String role - 角色
	 */
	public static String createToken(String username, String role, String deptId) {
		long expiration = EXPIRATION;
		System.out.println("有效時間: " + expiration);
		HashMap<String, Object> map = new HashMap<>();

		map.put(ROLE_CLAIMS, role);
		map.put(DEPTID, deptId);

		Date issDate = new Date(System.currentTimeMillis()); // 簽發日+7天
		log.info("建立Token時的簽發日: " + issDate);
//    	System.out.println("建立Token時的簽發日: "+issDate);
		Date expDate = new Date(System.currentTimeMillis() + expiration * 1000); // 過期日+7天
		log.info("建立Token欲設置的過期日: " + expDate);
//    	System.out.println("建立Token欲設置的過期日: "+expDate);

		return Jwts.builder().setClaims(map) // 角色
				.setIssuer(ISS) // 簽發者
				.setSubject(username) // 使用者名稱
				.setIssuedAt(issDate) // 簽發日期
				.setExpiration(expDate) // 過期時間
				.signWith(SignatureAlgorithm.HS512, SIGNATURE_WORD) // 密鑰簽名
				.compact();
	}

	// 從token中取得使用者名稱
	public static String getUsername(String token) {
		log.info("getUsername: " + getTokenBody(token).getSubject());
		log.info("TokenBody: " + getTokenBody(token));
		return getTokenBody(token).getSubject();
	}

	// 取得使用者角色
	public static String getUserRole(String token) {
		return (String) getTokenBody(token).get(ROLE_CLAIMS);
	}

	// 取得簽發日
	public static Date getIssAt(String token) {
		return getTokenBody(token).getIssuedAt();
	}

	// 取得部門別
	public static String getDeptId(String token) {
		return (String) getTokenBody(token).get(DEPTID);
	}

	// 取得過期日
	public static Date getExpDate(String token) {
		return getTokenBody(token).getExpiration();
	}

	// 是否已過期
	public static boolean isExpiration(String token) {
		return getTokenBody(token).getExpiration().before(new Date());
	}

	// 取得Token主體
	public static Claims getTokenBody(String token) {
//	 	Claims claims;
//	 	try {
//                 	claims = Jwts.parser().setSigningKey(SIGNATURE_WORD)
// 	                .parseClaimsJws(token).getBody();
//                 	System.out.println("Claims = "+claims);
//         	} catch (ExpiredJwtException e) {
//                 	claims = e.getClaims();
//         	}
//	 	return claims;
		return Jwts.parser().setSigningKey(SIGNATURE_WORD).parseClaimsJws(token).getBody();
	}

	// 解析 Token
	public static Map<String, Object> parseToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(SIGNATURE_WORD).parseClaimsJws(token).getBody();
		return claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
