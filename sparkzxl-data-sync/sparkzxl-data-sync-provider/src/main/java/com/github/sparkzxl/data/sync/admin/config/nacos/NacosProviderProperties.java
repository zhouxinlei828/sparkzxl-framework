package com.github.sparkzxl.data.sync.admin.config.nacos;

import lombok.Getter;
import lombok.Setter;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: the nacos sync strategy properties.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:16:33
 */
@Setter
@Getter
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_PROVIDER_PREFIX + "nacos")
public class NacosProviderProperties implements InitializingBean {

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
        NacosProviderProperties that = (NacosProviderProperties) o;
        return Objects.equals(url, that.url) && Objects.equals(namespace, that.namespace) && Objects.equals(username, that.username)
                && Objects.equals(password, that.password) && Objects.equals(acm, that.acm) && Objects.equals(watchConfigs,
                that.watchConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, namespace, username, password, acm, watchConfigs);
    }

    @Override
    public String toString() {
        return "NacosProviderProperties{" +
                "url='" + url + '\'' +
                ", namespace='" + namespace + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", acm=" + acm +
                ", watchConfigs=" + watchConfigs +
                '}';
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(watchConfigs)) {
            watchConfigs = Lists.newArrayList();
        }
    }
}
