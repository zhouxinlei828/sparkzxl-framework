package com.github.sparkzxl.feign.interceptor;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.context.BaseContextHolder;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    private final boolean seataEnable;

    public static final List<String> HEADER_NAME_LIST = Arrays.asList(
            BaseContextConstants.TENANT, BaseContextConstants.JWT_KEY_USER_ID,
            BaseContextConstants.JWT_KEY_ACCOUNT, BaseContextConstants.JWT_KEY_NAME,
            BaseContextConstants.TRACE_ID_HEADER, BaseContextConstants.JWT_TOKEN_HEADER, "X-Real-IP", "x-forwarded-for"
    );

    public FeignAddHeaderRequestInterceptor(boolean seataEnable) {
        this.seataEnable = seataEnable;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(BaseContextConstants.REMOTE_CALL, BaseContextConstants.REMOTE_CALL);
        if (seataEnable) {
            String xid = RootContext.getXID();
            if (StrUtil.isNotEmpty(xid)) {
                template.header(RootContext.KEY_XID, xid);
            }
        }
        ServletRequestAttributes requestAttributes = RequestContextHolderUtils.getRequestAttributes();
        if (requestAttributes == null) {
            HEADER_NAME_LIST.forEach((headerName) -> template.header(headerName, BaseContextHolder.get(headerName)));
            return;
        }
        HEADER_NAME_LIST.forEach((headerName) -> {
            String header = requestAttributes.getRequest().getHeader(headerName);
            template.header(headerName, StringUtils.isNotEmpty(header) ? header : BaseContextHolder.get(headerName));
        });
    }
}
