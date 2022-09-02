package com.guthub.sparkzxl.data.sync.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: Websocket配置
 *
 * @author zhouxinlei
 * @since 2022-08-25 14:00:39
 */
@Data
@ConfigurationProperties(prefix = "sparkzxl.data.sync.websocket")
public class WebsocketClientConfig {

    /**
     * if you have more admin url,please config like this.
     * 127.0.0.1:8888,127.0.0.1:8889
     */
    private String urls;

    /**
     * allowOrigin.
     */
    private String allowOrigin;
}
