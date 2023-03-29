package com.github.sparkzxl.web.interceptor;

import com.github.sparkzxl.web.properties.InterceptorProperties;
import com.google.common.collect.Lists;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.ModelAndView;

/**
 * description: 抽象内部拦截器
 *
 * @author zhouxinlei
 * @since 2022-12-08 20:13:31
 */
public abstract class AbstractInnerInterceptor implements InnerInterceptor {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private List<String> includePatterns = Lists.newArrayList("/**");

    private List<String> excludePatterns = Lists.newArrayList();

    @Override
    public void initInnerInterceptor(InterceptorProperties properties) {
        if (CollectionUtils.isNotEmpty(properties.getExcludePatterns())) {
            this.excludePatterns = properties.getExcludePatterns();
        }
        if (CollectionUtils.isNotEmpty(properties.getIncludePatterns())) {
            this.includePatterns = properties.getIncludePatterns();
        }
    }

    @Override
    public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        boolean match = excludePatterns.stream().noneMatch((url) -> antPathMatcher.match(url, requestUri) || requestUri.startsWith(url));
        boolean anyMatch = includePatterns.stream().anyMatch((url) -> antPathMatcher.match(url, requestUri) || requestUri.startsWith(url));
        if (match && anyMatch) {
            doPreHandle(request, response, handler);
        }
    }

    /**
     * 执行之前操作处理
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @throws Exception 异常
     */
    public abstract void doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        String requestUri = request.getRequestURI();
        boolean match = excludePatterns.stream().noneMatch((url) -> antPathMatcher.match(url, requestUri) || requestUri.startsWith(url));
        boolean anyMatch = includePatterns.stream().anyMatch((url) -> antPathMatcher.match(url, requestUri) || requestUri.startsWith(url));
        if (match && anyMatch) {
            doPostHandle(request, response, handler, modelAndView);
        }
    }

    /**
     * 执行之前操作处理
     *
     * @param request      request
     * @param response     response
     * @param handler      handler
     * @param modelAndView modelAndView
     * @throws Exception 异常
     */
    public abstract void doPostHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception;

}
