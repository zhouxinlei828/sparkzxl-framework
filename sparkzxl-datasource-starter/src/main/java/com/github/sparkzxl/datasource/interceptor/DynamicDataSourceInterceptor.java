package com.github.sparkzxl.datasource.interceptor;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.util.RequestContextHolderUtils;
import com.github.sparkzxl.datasource.provider.DataSourceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * description: 动态数据源拦截器
 *
 * @author zhoux
 */
@Slf4j
@RequiredArgsConstructor
public class DynamicDataSourceInterceptor implements HandlerInterceptor {

    private DataSourceProvider dataSourceProvider;
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired(required = false)
    public void setDynamicRoutingDataSource(DynamicRoutingDataSource dynamicRoutingDataSource) {
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
    }

    @Autowired(required = false)
    public void setDataSourceProvider(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = RequestContextHolderUtils.getHeader(BaseContextConstants.TENANT_ID);
        DynamicDataSourceContextHolder.poll();
        log.info("当前租户Id:{}", tenantId);
        if (StringUtils.isNotBlank(tenantId) && !dynamicRoutingDataSource.getDataSources().containsKey(tenantId)) {
            DataSource dataSource = dataSourceProvider.loadSelectedDataSource(tenantId);
            dynamicRoutingDataSource.addDataSource(tenantId, dataSource);
        }
        DynamicDataSourceContextHolder.push(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        DynamicDataSourceContextHolder.clear();
    }
}
