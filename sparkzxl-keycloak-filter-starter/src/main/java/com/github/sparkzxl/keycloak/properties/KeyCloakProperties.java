package com.github.sparkzxl.keycloak.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.cloud.keycloak")
public class KeyCloakProperties {

    private String realm;

    private String authServerUrl;

    private String tokenUrl;

    private String logoutUrl;


}
