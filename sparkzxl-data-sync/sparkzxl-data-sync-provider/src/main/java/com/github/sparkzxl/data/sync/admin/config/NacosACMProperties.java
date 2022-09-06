package com.github.sparkzxl.data.sync.admin.config;

import java.util.Objects;

/**
 * description: nacos configuration properties
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:00:07
 */
public class NacosACMProperties {

    private boolean enabled;

    private String endpoint;

    private String namespace;

    private String accessKey;

    private String secretKey;

    /**
     * get enabled.
     *
     * @return enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * set enabled.
     *
     * @param enabled enabled
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * get endpoint.
     *
     * @return endpoint
     */
    public String getEndpoint() {
        return endpoint;
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
     * get accessKey.
     *
     * @return accessKey
     */
    public String getAccessKey() {
        return accessKey;
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
     * get secretKey.
     *
     * @return secretKey
     */
    public String getSecretKey() {
        return secretKey;
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
