package com.sparksys.security.service;

import com.sparksys.core.constant.CacheKey;
import com.sparksys.core.entity.GlobalAuthUser;
import com.sparksys.core.utils.SpringContextUtils;
import com.sparksys.jwt.entity.JwtUserInfo;
import com.sparksys.core.repository.CacheRepository;
import com.sparksys.jwt.properties.JwtProperties;
import com.sparksys.jwt.service.JwtTokenService;
import com.sparksys.security.entity.AuthUserDetail;
import com.sparksys.security.event.LoginEvent;
import com.sparksys.security.entity.LoginStatus;
import com.sparksys.core.support.ResponseResultStatus;
import com.sparksys.core.constant.CoreConstant;
import com.sparksys.core.support.BusinessException;
import com.sparksys.core.utils.MD5Utils;
import com.sparksys.security.entity.AuthToken;
import com.sparksys.security.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.annotation.Resource;

/**
 * description: 登录授权Service
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:39:06
 */
@Slf4j
public abstract class AbstractAuthSecurityService {

    @Resource
    private CacheRepository cacheRepository;
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
        GlobalAuthUser authUser = adminUserDetails.getAuthUser();
        //校验密码输入是否正确
        checkPasswordError(authRequest, password, authUser);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(adminUserDetails,
                null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        token = createJwtToken(authUser);
        authUser.setPassword(null);
        AuthToken authToken = new AuthToken();
        authToken.setToken(token);
        authToken.setExpiration(CoreConstant.JwtTokenConstant.JWT_EXPIRATION);
        authToken.setAuthUser(authUser);
        //设置accessToken缓存
        accessToken(authToken, authUser);
        SpringContextUtils.publishEvent(new LoginEvent(LoginStatus.success(authUser.getId())));
        return authToken;
    }

    private String createJwtToken(GlobalAuthUser globalAuthUser) {
        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                .sub(globalAuthUser.getAccount())
                .iat(System.currentTimeMillis())
                .authorities(globalAuthUser.getPermissions())
                .username(globalAuthUser.getAccount())
                .expire(jwtProperties.getExpire())
                .build();
        return jwtTokenService.createTokenByHmac(jwtUserInfo);
    }

    private void checkPasswordError(LoginDTO authRequest, String password, GlobalAuthUser authUser) {
        String encryptPassword = MD5Utils.encrypt(authRequest.getPassword());
        log.info("密码加密 = {}，数据库密码={}", password, encryptPassword);
        //数据库密码比对
        boolean verifyResult = StringUtils.equals(encryptPassword, authUser.getPassword());
        if (!verifyResult) {
            SpringContextUtils.publishEvent(new LoginEvent(LoginStatus.pwdError(authUser.getId(),
                    ResponseResultStatus.PASSWORD_ERROR.getMessage())));
            ResponseResultStatus.PASSWORD_ERROR.assertNotTrue(false);
        }
    }

    /**
     * 设置accessToken缓存
     *
     * @param authToken 用户token
     * @param authUser  认证用户
     * @return void
     */
    private void accessToken(AuthToken authToken, GlobalAuthUser authUser) {
        String token = authToken.getToken();
        cacheRepository.set(CacheKey.buildKey(CacheKey.AUTH_USER, token), authUser,
                authToken.getExpiration());
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param account 用户名
     * @return AdminUserDetails
     * @throws BusinessException 异常
     */
    public abstract AuthUserDetail getAuthUserDetail(String account);

}
