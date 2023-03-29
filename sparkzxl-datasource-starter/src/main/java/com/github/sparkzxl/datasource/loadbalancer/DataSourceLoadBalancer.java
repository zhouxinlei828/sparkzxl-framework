package com.github.sparkzxl.datasource.loadbalancer;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import java.util.List;

/**
 * description: 数据源负载均衡策略
 *
 * @author zhouxinlei
 */
public interface DataSourceLoadBalancer {

    /**
     * 选择数据源
     *
     * @param dataSourceProperties 数据源属性列表
     * @return DataSourceProperty
     */
    DataSourceProperty choose(List<DataSourceProperty> dataSourceProperties);
}
