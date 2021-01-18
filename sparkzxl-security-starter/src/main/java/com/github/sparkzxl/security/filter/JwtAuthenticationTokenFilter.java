package com.github.sparkzxl.security.filter;

import com.github.sparkzxl.core.base.ResponseResultUtils;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.resource.SwaggerStaticResource;
import com.github.sparkzxl.core.utils.StringHandlerUtils;
import com.github.sparkzxl.jwt.entity.JwtUserInfo;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.security.entity.AuthUserDetail;
import com.github.sparkzxl.security.properties.SecurityProperties;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
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
import java.util.Arrays;

/**
 * description: jwt认证授权过滤器
 *
 * @author: zhouxinlei
 * @date: 2020-08-07 14:41:50
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private JwtTokenService jwtTokenService;


    public void setJwtTokenService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) {
        String accessToken = ResponseResultUtils.getAuthHeader(request);
        if (StringUtils.isNotEmpty(accessToken)) {
            JwtUserInfo jwtUserInfo = jwtTokenService.verifyTokenByHmac(accessToken);
            String username = jwtUserInfo.getUsername();
            log.info("checking username:{}", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                AuthUserDetail adminUserDetails = new AuthUserDetail(
                        jwtUserInfo.getId(),
                        jwtUserInfo.getUsername(),
                        null,
                        jwtUserInfo.getName(),
                        true,
                        jwtUserInfo.getAuthorities()
                );
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(adminUserDetails,
                        null, adminUserDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authenticated user:{}", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

}
