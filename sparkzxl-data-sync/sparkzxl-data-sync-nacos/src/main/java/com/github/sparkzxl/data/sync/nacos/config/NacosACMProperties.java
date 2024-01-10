package com.github.sparkzxl.data.sync.nacos.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * description: nacos configuration properties
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:00:07
 */
@Setter
@Getter
public class NacosACMProperties {

    private boolean enabled;

    private String endpoint;

    private String namespace;

    private String accessKey;

    private String secretKey;

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
