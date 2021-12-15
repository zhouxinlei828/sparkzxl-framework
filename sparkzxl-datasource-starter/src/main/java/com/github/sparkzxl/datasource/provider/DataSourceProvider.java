package com.github.sparkzxl.datasource.provider;

import javax.sql.DataSource;

/**
 * description: 数据源加载源
 *
 * @author zhouxinlei
 */
public interface DataSourceProvider {

    /**
     * 加载当前选择数据源
     *
     * @param tenantId 租户id
     * @return DataSourceProperty
     */
    DataSource loadSelectedDataSource(String tenantId);

}
