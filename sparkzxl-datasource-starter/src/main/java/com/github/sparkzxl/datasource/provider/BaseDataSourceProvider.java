package com.github.sparkzxl.datasource.provider;

import cn.hutool.core.text.StrFormatter;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.datasource.loadbalancer.DataSourceLoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.List;

/**
 * description: 抽象数据源策略实现
 *
 * @author zhouxinlei
 */
public abstract class BaseDataSourceProvider implements DataSourceProvider {

    private DataSourceCreator basicDataSourceCreator;
    private DataSourceLoadBalancer dataSourceLoadBalancer;

    @Autowired
    public void setDataSourceCreator(DataSourceCreator basicDataSourceCreator) {
        this.basicDataSourceCreator = basicDataSourceCreator;
    }

    @Autowired
    public void setDataSourceLoadBalancer(DataSourceLoadBalancer dataSourceLoadBalancer) {
        this.dataSourceLoadBalancer = dataSourceLoadBalancer;
    }

    public DataSource createDataSource(String tenantId, DataSourceProperty dataSourceProperty) {
        ArgumentAssert.notNull(dataSourceProperty, () -> new TenantException(StrFormatter.format("无此租户[{}]", tenantId)));
        String poolName = dataSourceProperty.getPoolName();
        if (poolName == null || "".equals(poolName)) {
            poolName = tenantId;
        }
        dataSourceProperty.setPoolName(poolName);
        return basicDataSourceCreator.createDataSource(dataSourceProperty);
    }

    public DataSourceProperty loadBalancerDataSource(List<DataSourceProperty> dataSourceProperties) {
        return dataSourceLoadBalancer.choose(dataSourceProperties);
    }
}
