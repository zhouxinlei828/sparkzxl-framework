package com.github.sparkzxl.data.sync.websocket.config;

import lombok.Data;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: Websocket配置
 *
 * @author zhouxinlei
 * @since 2022-08-25 14:00:39
 */
@Data
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_CONSUMER_PREFIX + "websocket")
public class WebsocketConsumerProperties {

    private boolean enabled;

    /**
     * 使用服务发现
     */
    private boolean useRegistry;

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * if you have more admin url,please config like this. 127.0.0.1:8888,127.0.0.1:8889
     */
    private String urls;

    /**
     * allowOrigin.
     */
    private String allowOrigin;
}
