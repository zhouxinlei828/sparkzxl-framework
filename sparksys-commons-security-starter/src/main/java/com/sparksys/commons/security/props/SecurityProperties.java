package com.sparksys.commons.security.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sparksys.security")
public class SecurityProperties {

    private boolean enableJwtFilter;

}
