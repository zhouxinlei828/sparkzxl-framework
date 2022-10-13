package com.github.sparkzxl.data.sync.admin.config;

import com.github.sparkzxl.data.sync.admin.DataSyncPushType;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: the websocket sync strategy properties.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:16:33
 */
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_PREFIX)
public class DataSyncProviderProperties {

    /**
     * default: true.
     */
    private DataSyncPushType type = DataSyncPushType.WEBSOCKET;

    public DataSyncPushType getType() {
        return type;
    }

    public void setType(DataSyncPushType type) {
        this.type = type;
    }
}
