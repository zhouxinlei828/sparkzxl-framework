package com.github.sparkzxl.datasource.provider;

import cn.hutool.core.text.StrFormatter;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.support.TenantException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

/**
 * description: YAML data source
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class YamlDataSourceProvider extends BaseDataSourceProvider {

    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Autowired
    public void setDynamicDataSourceProperties(DynamicDataSourceProperties dynamicDataSourceProperties) {
        this.dynamicDataSourceProperties = dynamicDataSourceProperties;
    }

    @Override
    public DataSource loadSelectedDataSource(String tenantId) {
        DataSourceProperty dataSourceProperty = dynamicDataSourceProperties.getDatasource().get(tenantId);
        ExceptionAssert.isEmpty(dataSourceProperty).withRuntimeException(new TenantException(StrFormatter.format("无此租户[{}]", tenantId)));
        return createDataSource(tenantId, dataSourceProperty);
    }
}
