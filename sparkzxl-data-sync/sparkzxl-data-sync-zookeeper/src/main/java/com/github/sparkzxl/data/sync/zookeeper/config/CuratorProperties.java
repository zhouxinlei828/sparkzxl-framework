package com.github.sparkzxl.data.sync.zookeeper.config;

import lombok.Getter;
import lombok.Setter;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Objects;

/**
 * description: zookeeper属性配置
 *
 * @author zhouxinlei
 */
@Setter
@Getter
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_CONSUMER_PREFIX + "zookeeper")
public class CuratorProperties {

    private boolean enabled;
    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 超时时间(毫秒)，默认1000
     */
    private Integer timeout = 1000;

    /**
     * zookeeper 地址
     */
    private String zkServers;

    /**
     * session超时时间
     */
    private Integer sessionTimeoutMs;

    /**
     * 连接超时时间
     */
    private Integer connectionTimeoutMs;

    private List<ZkWatchProperties> watchConfigs;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CuratorProperties that = (CuratorProperties) o;
        return Objects.equals(retryCount, that.retryCount) && Objects.equals(timeout, that.timeout) && Objects.equals(zkServers,
                that.zkServers) && Objects.equals(sessionTimeoutMs, that.sessionTimeoutMs) && Objects.equals(connectionTimeoutMs,
                that.connectionTimeoutMs) && Objects.equals(watchConfigs, that.watchConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retryCount, timeout, zkServers, sessionTimeoutMs, connectionTimeoutMs, watchConfigs);
    }

    @Override
    public String toString() {
        return "CuratorProperties{" +
                "retryCount=" + retryCount +
                ", timeout=" + timeout +
                ", zkServers='" + zkServers + '\'' +
                ", sessionTimeoutMs=" + sessionTimeoutMs +
                ", connectionTimeoutMs=" + connectionTimeoutMs +
                ", watchConfigs=" + watchConfigs +
                '}';
    }
}
