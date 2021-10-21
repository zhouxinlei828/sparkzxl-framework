package com.github.sparkzxl.datasource.interceptor;

import cn.hutool.core.text.StrFormatter;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.datasource.autoconfigure.DynamicDataProperties;
import com.github.sparkzxl.datasource.constant.DataSourceProviderEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.function.Function;

/**
 * description: 动态数据源拦截器
 *
 * @author zhoux
 */
@Slf4j
@RequiredArgsConstructor
public class DynamicDataSourceInterceptor implements HandlerInterceptor {

    private DynamicRoutingDataSource dynamicRoutingDataSource;

    private DataSourceCreator dataSourceCreator;

    private DynamicDataProperties dynamicDataProperties;

    private final Function<String, DataSourceProperty> function;

    @Autowired(required = false)
    public void setDynamicRoutingDataSource(DynamicRoutingDataSource dynamicRoutingDataSource) {
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
    }

    @Autowired(required = false)
    public void setDataSourceCreator(DataSourceCreator dataSourceCreator) {
        this.dataSourceCreator = dataSourceCreator;
    }

    @Autowired(required = false)
    public void setDynamicDataProperties(DynamicDataProperties dynamicDataProperties) {
        this.dynamicDataProperties = dynamicDataProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = RequestContextHolderUtils.getHeader(BaseContextConstants.TENANT_ID);
        DynamicDataSourceContextHolder.poll();
        log.info("当前租户Id:{}", tenantId);
        if (StringUtils.isNotBlank(tenantId) && !dynamicRoutingDataSource.getDataSources().containsKey(tenantId)) {
            if (dynamicDataProperties.getDataProvider().equals(DataSourceProviderEnum.JDBC)) {
                DataSourceProperty dataSourceProperty = function.apply(tenantId);
                ExceptionAssert.isEmpty(dataSourceProperty).withRuntimeException(new TenantException(StrFormatter.format("无此租户[{}]", tenantId)));
                DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
                dynamicRoutingDataSource.addDataSource(tenantId, dataSource);
            } else {
                ExceptionAssert.isTrue(dynamicDataProperties.getDataProvider().equals(DataSourceProviderEnum.MEMORY)).withRuntimeException(new TenantException(StrFormatter.format("无此租户[{}]", tenantId)));
            }
        }
        DynamicDataSourceContextHolder.push(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        DynamicDataSourceContextHolder.clear();
    }

    public static void main(String[] args) {
        String data = null;
        try {
            ExceptionAssert.isEmpty(data).withException(new TenantException(StrFormatter.format("无此租户[{}]", "dev")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
