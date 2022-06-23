package com.github.sparkzxl.datasource.provider;

import cn.hutool.core.text.StrFormatter;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.util.ArgumentAssert;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * description: 有JDBC缓存的数据源加载实现
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class JdbcCacheDataSourceProvider extends BaseDataSourceProvider {

    private final Function<String, List<DataSourceProperty>> function;
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public DataSource loadSelectedDataSource(String tenantId) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        DataSourceProperty dataSourceProperty = (DataSourceProperty) valueOperations.get(tenantId);
        if (ObjectUtils.isEmpty(dataSourceProperty)) {
            List<DataSourceProperty> dataSourceProperties = function.apply(tenantId);
            dataSourceProperty = loadBalancerDataSource(dataSourceProperties);
            ArgumentAssert.notNull(dataSourceProperty, () -> new TenantException(StrFormatter.format("无此租户[{}]", tenantId)));
            valueOperations.set(tenantId, dataSourceProperty, 1, TimeUnit.DAYS);
        }
        return createDataSource(tenantId, dataSourceProperty);
    }
}
