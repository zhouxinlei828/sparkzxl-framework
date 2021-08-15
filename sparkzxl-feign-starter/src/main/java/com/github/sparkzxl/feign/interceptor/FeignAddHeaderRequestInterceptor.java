package com.github.sparkzxl.feign.interceptor;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.AppContextHolder;
import com.github.sparkzxl.core.utils.StrPool;
import com.github.sparkzxl.feign.properties.FeignProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * description: feign client 拦截器，
 * 实现将 feign 调用方的 请求头封装到 被调用方的请求头
 *
 * @author zhouxinlei
 */
@Slf4j
public class FeignAddHeaderRequestInterceptor implements RequestInterceptor {

    public static final List<String> HEADER_NAME_LIST = Arrays.asList(
            BaseContextConstants.TENANT, BaseContextConstants.JWT_KEY_USER_ID,
            BaseContextConstants.JWT_KEY_ACCOUNT, BaseContextConstants.JWT_KEY_NAME,
            BaseContextConstants.TRACE_ID_HEADER, BaseContextConstants.JWT_TOKEN_HEADER, "X-Real-IP", "x-forwarded-for"
    );
    private FeignProperties feignProperties;

    public FeignAddHeaderRequestInterceptor() {
    }

    @Autowired
    public void setFeignProperties(FeignProperties feignProperties) {
        this.feignProperties = feignProperties;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(BaseContextConstants.REMOTE_CALL, StrPool.TRUE);
        if (feignProperties.isEnable()) {
            String xid = RootContext.getXID();
            if (StrUtil.isNotEmpty(xid)) {
                template.header(RootContext.KEY_XID, xid);
            }
        }
        RequestAttributes requestAttributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            HEADER_NAME_LIST.forEach((headerName) -> template.header(headerName, AppContextHolder.get(headerName)));
            return;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (request == null) {
            log.warn("path={}, 在FeignClient API接口未配置FeignConfiguration类， 故而无法在远程调用时获取请求头中的参数!", template.path());
            return;
        }
        HEADER_NAME_LIST.forEach((headerName) -> {
            String header = request.getHeader(headerName);
            template.header(headerName, StringUtils.isEmpty(header) ? AppContextHolder.get(headerName) : header);
        });
    }
}
