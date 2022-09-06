package com.github.sparkzxl.data.sync.admin.config;

import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;
import java.util.Objects;

/**
 * description: the nacos sync strategy properties.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:16:33
 */
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_PROVIDER_PREFIX + "nacos")
public class NacosProviderProperties implements InitializingBean {

    private String url;

    private String namespace;

    private String username;

    private String password;

    @NestedConfigurationProperty
    private NacosACMProperties acm;

    @NestedConfigurationProperty
    private List<NacosWatchProperties> watchConfigs;


    /**
     * Gets the value of url.
     *
     * @return the value of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Gets the value of namespace.
     *
     * @return the value of namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace.
     *
     * @param namespace namespace
     */
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    /**
     * Gets the value of username.
     *
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the value of password.
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password password
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Gets the value of acm.
     *
     * @return the value of acm
     */
    public NacosACMProperties getAcm() {
        return acm;
    }

    /**
     * Sets the acm.
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
        NacosProviderProperties that = (NacosProviderProperties) o;
        return Objects.equals(url, that.url) && Objects.equals(namespace, that.namespace) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(acm, that.acm) && Objects.equals(watchConfigs, that.watchConfigs);
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
