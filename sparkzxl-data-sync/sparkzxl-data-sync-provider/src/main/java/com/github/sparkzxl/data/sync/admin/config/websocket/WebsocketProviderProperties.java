package com.github.sparkzxl.data.sync.admin.config.websocket;

import lombok.Getter;
import lombok.Setter;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: the websocket sync strategy properties.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:16:33
 */
@Setter
@Getter
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_PROVIDER_PREFIX + "websocket")
public class WebsocketProviderProperties {

    private boolean enabled = true;

    private int messageMaxSize;

}
