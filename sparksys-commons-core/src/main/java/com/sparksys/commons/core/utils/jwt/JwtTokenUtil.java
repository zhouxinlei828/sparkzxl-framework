package com.sparksys.commons.core.utils.jwt;

import com.sparksys.commons.core.constant.CoreConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * description: JwtToken生成工具类
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:06:51
 */
@Slf4j
public class JwtTokenUtil {

    /**
     * 根据负责生成JWT的token
     *
     * @param claims
     * @return
     */
    private static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, CoreConstant.JwtTokenConstant.JWT_SECRET)
                .compact();
    }

    /**
     * 从token中获取JWT中的负载
     *
     * @param token
     * @return
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(CoreConstant.JwtTokenConstant.JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.info("JWT格式验证失败:{}", token);
        }
        return claims;
    }

    /**
     * 生成token的过期时间
     *
     * @return
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + CoreConstant.JwtTokenConstant.JWT_EXPIRATION * 1000);
    }

    /**
     * 从token中获取登录用户名
     *
     * @param token
     * @return
     */
    public static String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 验证token是否还有效
     *
     * @param token    客户端传入的token
     * @param userName 从数据库中查询出来的用户信息
     */
    public static boolean validateToken(String token, String userName) {
        String username = getUserNameFromToken(token);
        return username.equals(userName) && !isTokenExpired(token);
    }

    /**
     * 判断token是否已经失效
     *
     * @param token
     * @return
     */
    public static boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        if (ObjectUtils.isNotEmpty(expiredDate)) {
            return expiredDate.before(new Date());
        }
        return false;
    }

    /**
     * 从token中获取过期时间
     *
     * @param token
     * @return
     */
    private static Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (ObjectUtils.isNotEmpty(claims)) {
            return claims.getExpiration();
        }
        return null;
    }

    /**
     * 根据用户信息生成token
     *
     * @param userName
     * @return
     */
    public static String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(CoreConstant.JwtTokenConstant.CLAIM_KEY_USERNAME, userName);
        claims.put(CoreConstant.JwtTokenConstant.CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 判断token是否可以被刷新
     *
     * @param token
     * @return
     */
    public boolean canRefresh(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 刷新token
     *
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        claims.put(CoreConstant.JwtTokenConstant.CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }
}
