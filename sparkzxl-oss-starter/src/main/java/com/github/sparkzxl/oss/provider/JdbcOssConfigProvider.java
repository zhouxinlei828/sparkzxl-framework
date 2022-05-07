package com.github.sparkzxl.oss.provider;

import com.github.sparkzxl.oss.properties.OssConfigInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

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

    private final Function<String, List<OssConfigInfo>> function;

    @Override
    protected List<OssConfigInfo> get(String clientType) {
        return function.apply(clientType);
    }
}
