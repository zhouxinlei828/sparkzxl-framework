package com.sparksys.security.filter;

import com.sparksys.core.base.ResponseResultUtils;
import com.sparksys.jwt.entity.JwtUserInfo;
import com.sparksys.jwt.service.JwtTokenService;
import com.sparksys.security.entity.AuthUserDetail;
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
        if (StringUtils.isNotEmpty(accessToken)) {
            JwtUserInfo jwtUserInfo = jwtTokenService.verifyTokenByHmac(accessToken);
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
        chain.doFilter(request, response);
    }

}
