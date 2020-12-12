package com.github.sparkzxl.oauth.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.core.base.ResponseResultUtils;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.jwt.entity.JwtUserInfo;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.oauth.entity.AuthUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * description: jwt认证授权过滤器
 *
 * @author: zhouxinlei
 * @date: 2020-08-07 14:41:50
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationTokenFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String accessToken = ResponseResultUtils.getAuthHeader(request);
        if (!StringUtils.startsWith(accessToken, BaseContextConstants.BASIC_AUTH)) {
            if (StringUtils.isNotEmpty(accessToken)) {
                JwtUserInfo jwtUserInfo = null;
                try {
                    jwtUserInfo = jwtTokenService.getJwtUserInfo(accessToken);
                } catch (ParseException e) {
                    log.error("解析token用户信息出错 :{}", ExceptionUtil.getMessage(e));
                    SparkZxlExceptionAssert.businessFail("解析token用户信息出错");
                }
                assert jwtUserInfo != null;
                String username = jwtUserInfo.getUsername();
                log.info("checking username:{}", username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    AuthUserDetail adminUserDetails = (AuthUserDetail) this.userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(adminUserDetails,
                            null, adminUserDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("authenticated user:{}", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

}
