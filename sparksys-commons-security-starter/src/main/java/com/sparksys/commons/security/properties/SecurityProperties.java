package com.sparksys.commons.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(value = "sparksys.security")
public class SecurityProperties {

    private boolean enableJwtFilter;

}
