package com.github.sparkzxl.database;

import com.github.sparkzxl.database.echo.aspect.EchoResultAspect;
import com.github.sparkzxl.database.echo.core.EchoService;
import com.github.sparkzxl.database.echo.core.LoadService;
import com.github.sparkzxl.database.echo.properties.EchoProperties;
import com.github.sparkzxl.database.echo.typehandler.RemoteDataTypeHandler;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * description: 回显自动装配
 *
 * @author zhouxinlei
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(EchoProperties.class)
public class EchoAutoConfiguration {

    private final EchoProperties remoteProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = EchoProperties.PREFIX, name = "aop-enabled", havingValue = "true", matchIfMissing = true)
    public EchoResultAspect getEchoResultAspect(EchoService echoService) {
        return new EchoResultAspect(echoService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = EchoProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public EchoService getEchoService(Map<String, LoadService> strategyMap) {
        return new EchoService(remoteProperties, strategyMap);
    }

    /**
     * Mybatis 类型处理器： 处理 RemoteData 类型的字段
     */
    @Bean
    @ConditionalOnMissingBean
    public RemoteDataTypeHandler getRemoteDataTypeHandler() {
        return new RemoteDataTypeHandler();
    }
}
