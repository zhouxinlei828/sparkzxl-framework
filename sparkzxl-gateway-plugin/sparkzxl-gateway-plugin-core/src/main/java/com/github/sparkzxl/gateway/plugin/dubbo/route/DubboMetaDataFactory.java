package com.github.sparkzxl.gateway.plugin.dubbo.route;

import com.github.sparkzxl.gateway.common.entity.MetaData;
import org.springframework.cloud.gateway.route.Route;

/**
 * description: 用于构建Dubbo 元数据
 *
 * @author zhouxinlei
 * @since 2022-08-12 15:32:51
 */
public interface DubboMetaDataFactory {

    MetaData get(Route route);
}
