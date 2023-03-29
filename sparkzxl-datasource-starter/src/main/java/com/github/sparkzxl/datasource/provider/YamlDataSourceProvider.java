package com.github.sparkzxl.datasource.provider;

import cn.hutool.core.text.StrFormatter;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.util.ArgumentAssert;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

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
        ArgumentAssert.notNull(dataSourceProperty, () -> new TenantException(StrFormatter.format("无此租户[{}]", tenantId)));
        return createDataSource(tenantId, dataSourceProperty);
    }
}
