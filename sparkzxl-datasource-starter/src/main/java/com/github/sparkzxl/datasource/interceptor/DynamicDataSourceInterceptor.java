package com.github.sparkzxl.datasource.interceptor;

import cn.hutool.core.text.StrFormatter;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.github.sparkzxl.constant.AppContextConstants;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 动态数据源拦截器
 *
 * @author zhoux
 */
@Slf4j
public class DynamicDataSourceInterceptor implements HandlerInterceptor {

    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired(required = false)
    public void setDynamicRoutingDataSource(DynamicRoutingDataSource dynamicRoutingDataSource) {
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = RequestContextHolderUtils.getHeader(AppContextConstants.TENANT_ID);
        DynamicDataSourceContextHolder.poll();
        log.info("当前租户Id:{}", tenantId);
        if (StringUtils.isNotBlank(tenantId)) {
            if (!dynamicRoutingDataSource.getDataSources().containsKey(tenantId)) {
                throw new TenantException(StrFormatter.format("无此租户[{}]", tenantId));
            }
            DynamicDataSourceContextHolder.push(tenantId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        DynamicDataSourceContextHolder.clear();
    }
}
