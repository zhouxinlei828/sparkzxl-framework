package com.github.sparkzxl.boot.undertow;

import io.undertow.UndertowOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2023-06-04 08:48:33
 */
public class DefaultWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableUndertowWebServerFactory> {

    private final ServerProperties serverProperties;

    public DefaultWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    public void customize(ConfigurableUndertowWebServerFactory factory) {
        String pattern = serverProperties.getUndertow()
                .getAccesslog().getPattern();
        // 如果 accesslog 配置中打印了响应时间，则打开记录请求开始时间配置
        if (logRequestProcessingTiming(pattern)) {
            factory.addBuilderCustomizers(builder ->
                    builder.setServerOption(
                            UndertowOptions.RECORD_REQUEST_START_TIME,
                            true
                    )
            );
        }
    }

    private boolean logRequestProcessingTiming(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return false;
        }
        return pattern.contains("%D") || pattern.contains("%T");
    }
}
