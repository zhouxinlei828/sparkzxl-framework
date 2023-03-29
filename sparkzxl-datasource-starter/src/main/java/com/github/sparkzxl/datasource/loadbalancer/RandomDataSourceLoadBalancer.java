package com.github.sparkzxl.datasource.loadbalancer;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: 随机负载均衡数据源选择算法
 *
 * @author zhouxinlei
 */
public class RandomDataSourceLoadBalancer implements DataSourceLoadBalancer {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public DataSourceProperty choose(List<DataSourceProperty> dataSourceProperties) {
        return dataSourceProperties.get(Math.abs(index.getAndAdd(1) % dataSourceProperties.size()));
    }
}
