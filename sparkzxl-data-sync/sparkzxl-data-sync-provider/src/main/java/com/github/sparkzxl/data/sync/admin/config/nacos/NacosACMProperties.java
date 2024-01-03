package com.github.sparkzxl.data.sync.admin.config.nacos;

import lombok.Getter;

import java.util.Objects;

/**
 * description: nacos configuration properties
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:00:07
 */
@Getter
public class NacosACMProperties {

    /**
     * -- GETTER --
     * get enabled.
     */
    private boolean enabled;

    /**
     * -- GETTER --
     * get endpoint.
     */
    private String endpoint;

    /**
     * -- GETTER --
     * get namespace.
     */
    private String namespace;

    /**
     * -- GETTER --
     * get accessKey.
     */
    private String accessKey;

    /**
     * -- GETTER --
     * get secretKey.
     */
    private String secretKey;

    /**
     * set enabled.
     *
     * @param enabled enabled
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * set endpoint.
     *
     * @param endpoint endpoint
     */
    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
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
     * set accessKey.
     *
     * @param accessKey accessKey
     */
    public void setAccessKey(final String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * set secretKey.
     *
     * @param secretKey secretKey
     */
    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NacosACMProperties that = (NacosACMProperties) o;
        return enabled == that.enabled
                && Objects.equals(endpoint, that.endpoint)
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(accessKey, that.accessKey)
                && Objects.equals(secretKey, that.secretKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, endpoint, namespace, accessKey, secretKey);
    }

    @Override
    public String toString() {
        return "NacosACMConfig{"
                + "enabled="
                + enabled
                + ", endpoint='"
                + endpoint
                + '\''
                + ", namespace='"
                + namespace
                + '\''
                + ", accessKey='"
                + accessKey
                + '\''
                + ", secretKey='"
                + secretKey
                + '\''
                + '}';
    }
}
