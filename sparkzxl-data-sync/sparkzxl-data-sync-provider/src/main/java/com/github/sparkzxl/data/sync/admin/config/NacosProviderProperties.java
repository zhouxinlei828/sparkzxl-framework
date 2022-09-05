package com.github.sparkzxl.data.sync.admin.config;

import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: the nacos sync strategy properties.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:16:33
 */
@ConfigurationProperties(prefix = ConfigConstant.DATA_SYNC_PROVIDER_PREFIX + "nacos")
public class NacosProviderProperties {

    private String url;

    private String namespace;

    private String username;

    private String password;

    private NacosACMProperties acm;


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

    public static class NacosACMProperties {

        private boolean enabled;

        private String endpoint;

        private String namespace;

        private String accessKey;

        private String secretKey;

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
         * Gets the value of endpoint.
         *
         * @return the value of endpoint
         */
        public String getEndpoint() {
            return endpoint;
        }

        /**
         * Sets the endpoint.
         *
         * @param endpoint endpoint
         */
        public void setEndpoint(final String endpoint) {
            this.endpoint = endpoint;
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
         * Gets the value of accessKey.
         *
         * @return the value of accessKey
         */
        public String getAccessKey() {
            return accessKey;
        }

        /**
         * Sets the accessKey.
         *
         * @param accessKey accessKey
         */
        public void setAccessKey(final String accessKey) {
            this.accessKey = accessKey;
        }

        /**
         * Gets the value of secretKey.
         *
         * @return the value of secretKey
         */
        public String getSecretKey() {
            return secretKey;
        }

        /**
         * Sets the secretKey.
         *
         * @param secretKey secretKey
         */
        public void setSecretKey(final String secretKey) {
            this.secretKey = secretKey;
        }
    }
}
