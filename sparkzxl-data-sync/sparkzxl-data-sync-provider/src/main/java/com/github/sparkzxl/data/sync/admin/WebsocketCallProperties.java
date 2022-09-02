package com.github.sparkzxl.data.sync.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: the websocket sync strategy properties.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:16:33
 */
@ConfigurationProperties(prefix = "sparkzxl.data.provider.websocket")
public class WebsocketCallProperties {

    /**
     * default: true.
     */
    private boolean enabled = true;

    /**
     * default is 8192.
     */
    private int messageMaxSize;

    /**
     * Gets the value of enabled.
     *
     * @return the value of enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled enabled
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * get messageMaxSize.
     *
     * @return messageMaxSize
     */
    public int getMessageMaxSize() {
        return messageMaxSize;
    }

    /**
     * set messageMaxSize.
     *
     * @param messageMaxSize messageMaxSize
     */
    public void setMessageMaxSize(final int messageMaxSize) {
        this.messageMaxSize = messageMaxSize;
    }
}
