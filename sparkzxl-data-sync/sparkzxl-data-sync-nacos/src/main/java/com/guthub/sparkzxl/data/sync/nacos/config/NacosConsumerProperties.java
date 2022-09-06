package com.guthub.sparkzxl.data.sync.nacos.config;

import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;
import java.util.Objects;

/**
 * description: nacos client Properties
 *
 * @author zhouxinlei
 * @since 2022-09-06 09:59:23
 */
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_CONSUMER_PREFIX + "nacos")
public class NacosConsumerProperties {

    private String url;

    private String namespace;

    private String username;

    private String password;

    @NestedConfigurationProperty
    private NacosACMProperties acm;

    @NestedConfigurationProperty
    private List<NacosWatchProperties> watchConfigs;

    /**
     * get url.
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * set url.
     *
     * @param url url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * get namespace.
     *
     * @return namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * set namespace.
     *
     * @param namespace namespace
     */
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    /**
     * get username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * set username.
     *
     * @param username username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * get password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * set password.
     *
     * @param password password
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * get acm.
     *
     * @return acm
     */
    public NacosACMProperties getAcm() {
        return acm;
    }

    /**
     * set acm.
     *
     * @param acm acm
     */
    public void setAcm(final NacosACMProperties acm) {
        this.acm = acm;
    }

    public List<NacosWatchProperties> getWatchConfigs() {
        return watchConfigs;
    }

    public void setWatchConfigs(List<NacosWatchProperties> watchConfigs) {
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
