package com.guthub.sparkzxl.data.sync.zookeeper.config;

import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: zookeeper属性配置
 *
 * @author zhouxinlei
 */
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_CONSUMER_PREFIX + "zookeeper")
public class CuratorProperties {

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

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public Integer getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(Integer sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public Integer getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(Integer connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public List<ZkWatchProperties> getWatchConfigs() {
        return watchConfigs;
    }

    public void setWatchConfigs(List<ZkWatchProperties> watchConfigs) {
        this.watchConfigs = watchConfigs;
    }

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
