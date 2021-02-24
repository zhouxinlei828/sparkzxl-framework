package com.github.sparkzxl.open.filter;

import com.github.sparkzxl.core.context.BaseContextConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * description: 解决permitAll依旧校验Token的正确性问题
 * 思路：只需定义一个比OAuth2AuthenticationProcessingFilter更早的过滤器拦截指定请求，去除header中的Authorization Bearer xxxx即可
 *
 * @author: zhouxinlei
 * @date: 2021-02-24 09:29:28
 */
@Slf4j
public class PermitAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("当前访问的地址:{}", request.getRequestURI());
        if ("/permitAll".equals(request.getRequestURI())) {

            request = new HttpServletRequestWrapper(request) {
                private Set<String> headerNameSet;

                @Override
                public Enumeration<String> getHeaderNames() {
                    if (headerNameSet == null) {
                        // first time this method is called, cache the wrapped request's header names:
                        headerNameSet = new HashSet<>();
                        Enumeration<String> wrappedHeaderNames = super.getHeaderNames();
                        while (wrappedHeaderNames.hasMoreElements()) {
                            String headerName = wrappedHeaderNames.nextElement();
                            if (!BaseContextConstants.JWT_TOKEN_HEADER.equalsIgnoreCase(headerName)) {
                                headerNameSet.add(headerName);
                            }
                        }
                    }
                    return Collections.enumeration(headerNameSet);
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    if (BaseContextConstants.JWT_TOKEN_HEADER.equalsIgnoreCase(name)) {
                        return Collections.emptyEnumeration();
                    }
                    return super.getHeaders(name);
                }

                @Override
                public String getHeader(String name) {
                    if (BaseContextConstants.JWT_TOKEN_HEADER.equalsIgnoreCase(name)) {
                        return null;
                    }
                    return super.getHeader(name);
                }
            };

        }
        filterChain.doFilter(request, response);
    }
}
