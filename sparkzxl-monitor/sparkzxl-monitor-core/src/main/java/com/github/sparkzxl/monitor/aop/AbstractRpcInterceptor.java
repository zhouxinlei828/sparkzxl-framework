package com.github.sparkzxl.monitor.aop;

import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.sparkzxl.core.context.IContextHolder;
import com.github.sparkzxl.monitor.constant.InterceptorType;
import com.github.sparkzxl.monitor.constant.StrategyConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:42:18
 */
public abstract class AbstractRpcInterceptor {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractRpcInterceptor.class);

    @Autowired
    protected ConfigurableEnvironment environment;
    @Autowired
    protected IContextHolder contextHolder;
    @Value("${" + StrategyConstant.SPRING_MONITOR_REST_INTERCEPT_DEBUG_ENABLED + ":false}")
    protected Boolean interceptDebugEnabled;

    protected List<String> requestHeaderList = new ArrayList<>();

    public AbstractRpcInterceptor(String contextRequestHeaders, String businessRequestHeaders) {
        if (StringUtils.isNotEmpty(contextRequestHeaders)) {
            requestHeaderList.addAll(StringUtil.splitToList(contextRequestHeaders.toLowerCase()));
        }
        if (StringUtils.isNotEmpty(businessRequestHeaders)) {
            requestHeaderList.addAll(StringUtil.splitToList(businessRequestHeaders.toLowerCase()));
        }

        InterceptorType interceptorType = getInterceptorType();
        logger.info("--------- Intercept Information ---------");
        logger.info("{} desires to intercept customer headers are {}", interceptorType, requestHeaderList);
        logger.info("--------------------------------------------------");
    }

    protected void interceptInputHeader() {
        if (!interceptDebugEnabled) {
            return;
        }
        StringBuilder logInfoBuilder = new StringBuilder();
        Enumeration<String> headerNames = contextHolder.getHeaderNames();
        if (headerNames != null) {
            InterceptorType interceptorType = getInterceptorType();
            switch (interceptorType) {
                case FEIGN:
                    logInfoBuilder.append("--------- Feign Intercept Input Header Information ---------\n");
                    break;
                case REST_TEMPLATE:
                    logInfoBuilder.append("----- RestTemplate Intercept Input Header Information ------\n");
                    break;
                case WEB_CLIENT:
                    logInfoBuilder.append("------- WebClient Intercept Input Header Information -------\n");
                    break;
                default:
                    break;
            }
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                boolean isHeaderContains = isHeaderContains(headerName.toLowerCase());
                if (isHeaderContains) {
                    String headerValue = contextHolder.getHeader(headerName);
                    logInfoBuilder.append(headerName)
                            .append(":")
                            .append(headerValue)
                            .append("\n");
                }
            }
            logInfoBuilder.append("------------------------------------------------------------");
        }
        if (StringUtils.isNotEmpty(logInfoBuilder.toString())) {
            logger.info(logInfoBuilder.toString());
        }
    }

    protected boolean isHeaderContains(String headerName) {
        return requestHeaderList.contains(headerName);
    }

    protected abstract InterceptorType getInterceptorType();

}
