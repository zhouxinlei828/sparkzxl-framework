package com.github.sparkzxl.web.interceptor;

import com.github.sparkzxl.spi.SPI;
import com.github.sparkzxl.web.properties.InterceptorProperties;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/***
 * description: 内部拦截器
 *
 * @author zhouxinlei
 * @since 2022-12-08 19:26:53
 */
@SPI
public interface InnerInterceptor extends Ordered {


    /**
     * 初始化拦截器
     *
     * @param properties 拦截器配置
     */
    void initInnerInterceptor(InterceptorProperties properties);

    /**
     * 预处理
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @throws Exception 异常
     */
    void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

    /**
     * 成功后执行
     *
     * @param request      request
     * @param response     response
     * @param handler      handler
     * @param modelAndView modelAndView
     * @throws Exception 异常
     */
    void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception;

}
