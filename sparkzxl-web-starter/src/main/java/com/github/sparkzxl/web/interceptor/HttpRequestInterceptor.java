package com.github.sparkzxl.web.interceptor;

import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.constant.enums.RpcType;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.spi.ExtensionLoader;
import com.github.sparkzxl.web.properties.InterceptorProperties;
import com.github.sparkzxl.web.properties.WebProperties;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: http request interceptor
 *
 * @author zhouxinlei
 * @since 2022-12-08 19:24:09
 */
@Getter
@Setter
public class HttpRequestInterceptor implements AsyncHandlerInterceptor {

    private final List<InnerInterceptor> innerInterceptorList = Lists.newArrayList();

    public HttpRequestInterceptor(WebProperties webProperties) {
        List<InterceptorProperties> interceptorConfigList = webProperties.getInterceptorConfigList();
        Map<String, InterceptorProperties> interceptorPropertiesMap = interceptorConfigList.stream()
                .collect(Collectors.toMap(InterceptorProperties::getName, k -> k));
        List<InnerInterceptor> interceptorList = Lists.newArrayList();
        // 默认请求上下文传递
        RequestContextInnerInterceptor contextInnerInterceptor = new RequestContextInnerInterceptor();
        InterceptorProperties interceptorProperties = new InterceptorProperties();
        interceptorProperties.setIncludePatterns(Lists.newArrayList("/**"));
        contextInnerInterceptor.initInnerInterceptor(interceptorProperties);
        interceptorList.add(contextInnerInterceptor);
        for (Map.Entry<String, InterceptorProperties> entry : interceptorPropertiesMap.entrySet()) {
            InnerInterceptor innerInterceptor = ExtensionLoader.getExtensionLoader(InnerInterceptor.class).getJoin(entry.getKey());
            if (innerInterceptor == null) {
                throw new RuntimeException(StrFormatter.format("未装配[{}]", entry.getKey()));
            }
            InterceptorProperties properties = entry.getValue();
            innerInterceptor.initInnerInterceptor(properties);
            interceptorList.add(innerInterceptor);
        }
        if (CollectionUtils.isNotEmpty(interceptorList)) {
            this.innerInterceptorList.addAll(interceptorList.stream().sorted(Comparator.comparing(Ordered::getOrder)).collect(Collectors.toList()));
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestLocalContextHolder.set(BaseContextConstants.RPC_TYPE, RpcType.HTTP.getCode());
        for (InnerInterceptor innerInterceptor : innerInterceptorList) {
            innerInterceptor.preHandle(request, response, handler);
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        for (InnerInterceptor innerInterceptor : innerInterceptorList) {
            innerInterceptor.postHandle(request, response, handler, modelAndView);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestLocalContextHolder.remove();
    }


}
