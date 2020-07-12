package com.sparksys.commons.cloud.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * description: feign client 拦截器，
 * 实现将 feign 调用方的 请求头封装到 被调用方的请求头
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 16:31:14
 */
@Slf4j
public class FeignAddHeaderRequestInterceptor implements RequestInterceptor {

    public FeignAddHeaderRequestInterceptor() {
        super();
    }

    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (request == null) {
            log.warn("path={}, 在FeignClient API接口未配置FeignConfiguration类， 故而无法在远程调用时获取请求头中的参数!", template.path());
            return;
        }
    }
}
