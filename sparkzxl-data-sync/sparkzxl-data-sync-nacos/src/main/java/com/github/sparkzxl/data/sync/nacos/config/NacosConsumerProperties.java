package com.github.sparkzxl.data.sync.nacos.config;

import lombok.Getter;
import lombok.Setter;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Objects;

/**
 * description: nacos client Properties
 *
 * @author zhouxinlei
 * @since 2022-09-06 09:59:23
 */
@Getter
@Setter
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_CONSUMER_PREFIX + "nacos")
public class NacosConsumerProperties {

    private boolean enabled;

    private String url;

    private String namespace;

    private String username;

    private String password;

    private NacosACMProperties acm;

    private List<NacosWatchProperties> watchConfigs;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NacosConsumerProperties that = (NacosConsumerProperties) o;
        return Objects.equals(url, that.url)
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(username, that.username)
                && Objects.equals(password, that.password)
                && Objects.equals(acm, that.acm)
                && Objects.equals(watchConfigs, that.watchConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, namespace, username, password, acm, watchConfigs);
    }

    @Override
    public String toString() {
        return "NacosClientProperties{" +
                "url='" + url + '\'' +
                ", namespace='" + namespace + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", acm=" + acm +
                ", watchConfigs=" + watchConfigs +
                '}';
    }
}
