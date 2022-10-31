package com.github.sparkzxl.feign.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.feign.properties.FeignProperties;
import com.google.common.net.HttpHeaders;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * description: feign client 拦截器，
 * 实现将 feign 调用方的 请求头封装到 被调用方的请求头
 *
 * @author zhouxinlei
 */
@Slf4j
public class FeignHeaderRequestInterceptor implements RequestInterceptor {

    public static final List<String> HEADER_NAME_LIST = Arrays.asList(
            BaseContextConstants.TENANT_ID,
            BaseContextConstants.VERSION,
            BaseContextConstants.JWT_TOKEN_HEADER,
            "X-Real-IP",
            HttpHeaders.X_FORWARDED_FOR
    );
    private FeignProperties feignProperties;

    public FeignHeaderRequestInterceptor() {
    }

    @Autowired
    public void setFeignProperties(FeignProperties feignProperties) {
        this.feignProperties = feignProperties;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(BaseContextConstants.REMOTE_CALL, StrPool.TRUE);
        if (feignProperties.getSeata().isEnabled()) {
            String xid = RootContext.getXID();
            log.info("当前XID：{}", xid);
            if (StrUtil.isNotEmpty(xid)) {
                template.header(RootContext.KEY_XID, xid);
            }
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            Map<String, Object> localMap = RequestLocalContextHolder.getLocalMap();
            localMap.forEach((key, value) -> template.header(key, URLUtil.encode(Convert.toStr(value))));
            return;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (request == null) {
            log.warn("path={}, 在FeignClient API接口未配置FeignConfiguration类， 故而无法在远程调用时获取请求头中的参数!", template.path());
            return;
        }
        HEADER_NAME_LIST.forEach((headerName) -> {
            String header = request.getHeader(headerName);
            template.header(headerName, StringUtils.isEmpty(header) ? RequestLocalContextHolder.get(headerName) : header);
        });
        List<String> headerList = feignProperties.getInterceptor().getHeaderList();
        if (CollectionUtils.isNotEmpty(headerList)) {
            headerList.forEach((headerName) -> {
                String header = request.getHeader(headerName);
                template.header(headerName, StringUtils.isEmpty(header) ? URLUtil.encode(RequestLocalContextHolder.get(headerName)) : header);
            });
        }
    }
}
