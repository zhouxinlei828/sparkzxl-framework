package com.sparksys.security.service;

import cn.hutool.core.date.DateUtil;
import com.sparksys.core.constant.BaseContextConstants;
import com.sparksys.core.utils.DateUtils;
import com.sparksys.core.utils.KeyUtils;
import com.sparksys.core.entity.AuthUserInfo;
import com.sparksys.core.utils.SpringContextUtils;
import com.sparksys.jwt.entity.JwtUserInfo;
import com.sparksys.core.cache.CacheTemplate;
import com.sparksys.jwt.properties.JwtProperties;
import com.sparksys.jwt.service.JwtTokenService;
import com.sparksys.security.entity.AuthUserDetail;
import com.sparksys.security.event.LoginEvent;
import com.sparksys.security.entity.LoginStatus;
import com.sparksys.core.support.ResponseResultStatus;
import com.sparksys.core.support.BusinessException;
import com.sparksys.core.utils.Md5Utils;
import com.sparksys.security.entity.AuthToken;
import com.sparksys.security.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.util.Date;

/**
 * description: 登录授权Service
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:39:06
 */
@Slf4j
public abstract class AbstractAuthSecurityService {

    @Resource
    private CacheTemplate cacheRepository;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private JwtTokenService jwtTokenService;

    /**
     * 登录
     *
     * @param authRequest 登录认证
     * @return java.lang.String
     * @throws Exception 异常
     */
    public AuthToken login(LoginDTO authRequest) {
        String account = authRequest.getAccount();
        String password = authRequest.getPassword();
        String token;
        AuthUserDetail adminUserDetails = getAuthUserDetail(account);
        ResponseResultStatus.ACCOUNT_EMPTY.assertNotNull(adminUserDetails);
        AuthUserInfo authUserInfo = adminUserDetails.getAuthUserInfo();
        //校验密码输入是否正确
        checkPasswordError(authRequest, authUserInfo);
        AuthToken authToken = authorization(adminUserDetails, authUserInfo);
        SpringContextUtils.publishEvent(new LoginEvent(LoginStatus.success(authUserInfo.getId())));
        return authToken;
    }

    /**
     * 授权登录获取token
     *
     * @param adminUserDetails 授权用户
     * @param authUserInfo     全局用户信息
     * @return AuthToken
     */
    public AuthToken authorization(AuthUserDetail adminUserDetails, AuthUserInfo authUserInfo) {
        String token;
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(adminUserDetails,
                null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        token = createJwtToken(authUserInfo);
        authUserInfo.setPassword(null);
        AuthToken authToken = new AuthToken();
        authToken.setToken(token);
        authToken.setExpiration(jwtProperties.getExpire());
        authToken.setAuthUser(authUserInfo);
        //设置accessToken缓存
        accessToken(authToken, authUserInfo);
        return authToken;
    }


    private String createJwtToken(AuthUserInfo globalAuthUser) {
        Date expire = DateUtil.offsetSecond(new Date(), jwtProperties.getExpire().intValue());
        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                .sub(globalAuthUser.getAccount())
                .iat(System.currentTimeMillis())
                .authorities(globalAuthUser.getAuthorityList())
                .username(globalAuthUser.getAccount())
                .expire(expire)
                .build();
        return jwtTokenService.createTokenByHmac(jwtUserInfo);
    }

    private void checkPasswordError(LoginDTO authRequest, AuthUserInfo authUserInfo) {
        String encryptPassword = Md5Utils.encrypt(authRequest.getPassword());
        log.info("密码加密 = {}，数据库密码={}", encryptPassword, authUserInfo.getPassword());
        //数据库密码比对
        boolean verifyResult = StringUtils.equals(encryptPassword, authUserInfo.getPassword());
        if (!verifyResult) {
            SpringContextUtils.publishEvent(new LoginEvent(LoginStatus.pwdError(authUserInfo.getId(),
                    ResponseResultStatus.PASSWORD_ERROR.getMessage())));
            ResponseResultStatus.PASSWORD_ERROR.assertNotTrue(false);
        }
    }

    /**
     * 设置accessToken缓存
     *
     * @param authToken 用户token
     * @param authUser  认证用户
     */
    private void accessToken(AuthToken authToken, AuthUserInfo authUser) {
        String token = authToken.getToken();
        cacheRepository.set(KeyUtils.buildKey(BaseContextConstants.AUTH_USER, token), authUser,
                (long) authToken.getExpiration());
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param account 用户名
     * @return AdminUserDetails
     * @throws BusinessException 异常
     */
    public abstract AuthUserDetail getAuthUserDetail(String account);

    public static void main(String[] args) {
        Date exp = DateUtil.offsetSecond(new Date(), 60 * 60 * 24);
        System.out.println(DateUtils.format(exp, "yyyy-MM-dd HH:mm:ss"));
    }
}
