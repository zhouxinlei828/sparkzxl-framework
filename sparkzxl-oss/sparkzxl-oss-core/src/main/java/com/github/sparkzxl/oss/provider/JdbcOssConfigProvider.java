package com.github.sparkzxl.oss.provider;

import com.github.sparkzxl.oss.properties.Configuration;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * description: jdbc 加载oss 配置信息
 *
 * @author zhouxinlei
 * @since 2022-05-05 13:56:54
 */
@Getter
@Setter
@RequiredArgsConstructor
public class JdbcOssConfigProvider extends AbstractOssConfigProvider {

    private final Function<String, Configuration> function;
    private final Supplier<List<Configuration>> supplier;
    private final List<Configuration> configList;

    public JdbcOssConfigProvider(Function<String, Configuration> function,
            Supplier<List<Configuration>> supplier) {
        this.configList = Lists.newArrayList();
        this.function = function;
        this.supplier = supplier;
    }

    @Override
    public Configuration load(String clientId) {
        Optional<Configuration> optional = configList.stream().filter(config -> config.getClientId().equals(clientId)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        Configuration configuration = function.apply(clientId);
        configList.add(configuration);
        return configuration;
    }

    @Override
    protected List<Configuration> list() {
        return supplier.get();
    }
}
