package com.sparksys.commons.security.component;

import com.sparksys.commons.core.entity.GlobalAuthUser;
import com.sparksys.commons.core.utils.ResponseResultUtils;
import com.sparksys.commons.core.utils.crypto.MD5Utils;
import com.sparksys.commons.jwt.config.entity.JwtUserInfo;
import com.sparksys.commons.jwt.config.service.JwtTokenService;
import com.sparksys.commons.security.entity.AuthUserDetail;
import com.sparksys.commons.security.registry.SecurityRegistry;
import com.sparksys.commons.security.service.AbstractAuthSecurityService;

import com.sparksys.commons.user.service.IGlobalUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: JWT登录授权过滤器
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:34:44
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private AbstractAuthSecurityService abstractSecurityAuthDetailService;

    @Resource
    private IGlobalUserService globalUserService;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private SecurityRegistry securityRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) {
        try {
            log.info("请求地址：{}", request.getRequestURI());
            if (!securityRegistry.isIgnoreToken(request.getRequestURI())) {
                String accessToken = ResponseResultUtils.getAuthHeader(request);
                if (StringUtils.isNotEmpty(accessToken)) {
                    jwtTokenService.verifyTokenByHmac(accessToken, MD5Utils.encrypt("123456"));
                    JwtUserInfo jwtUserInfo  = jwtTokenService.verifyTokenByHmac(accessToken, MD5Utils.encrypt("123456"));
                    String username = jwtUserInfo.getUsername();
                    log.info("checking username:{}", username);
                    GlobalAuthUser authUser = globalUserService.getUserInfo(accessToken);
                    if (StringUtils.equals(authUser.getAccount(), username)) {
                        AuthUserDetail authUserDetail = abstractSecurityAuthDetailService.getAuthUserDetail(username);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(authUserDetail, null, authUserDetail.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        log.info("authenticated user:{}", username);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            ResponseResultUtils.unauthorized(response);
        }
    }
}
