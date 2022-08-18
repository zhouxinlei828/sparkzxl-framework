package com.github.sparkzxl.monitor.aop;

import com.github.sparkzxl.monitor.constant.InterceptorType;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:40:08
 */
public class FeignMonitorRpcInterceptor extends AbstractRpcInterceptor implements RequestInterceptor {

    public FeignMonitorRpcInterceptor(String contextRequestHeaders, String businessRequestHeaders) {
        super(contextRequestHeaders, businessRequestHeaders);
    }

    @Override
    public void apply(RequestTemplate template) {
        // 拦截打印输入的Header
        interceptInputHeader();

        // 拦截打印输出的Header
        interceptOutputHeader(template);
    }


    private void interceptOutputHeader(RequestTemplate requestTemplate) {
        if (!interceptDebugEnabled) {
            return;
        }
        StringBuilder logInfoBuilder = new StringBuilder();
        logInfoBuilder.append("-------- Feign Intercept Output Header Information ---------\n");
        Map<String, Collection<String>> headers = requestTemplate.headers();
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            boolean isHeaderContains = isHeaderContains(headerName.toLowerCase());
            if (isHeaderContains) {
                Collection<String> headerValue = entry.getValue();
                logInfoBuilder.append(headerName).append(":").append(headerValue);
            }
        }
        logInfoBuilder.append("------------------------------------------------------------");
        if (StringUtils.isNotEmpty(logInfoBuilder.toString())) {
            logger.info(logInfoBuilder.toString());
        }
    }

    @Override
    protected InterceptorType getInterceptorType() {
        return InterceptorType.FEIGN;
    }
}
