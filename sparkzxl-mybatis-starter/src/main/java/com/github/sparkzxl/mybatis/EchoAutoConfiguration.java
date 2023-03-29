package com.github.sparkzxl.mybatis;

import com.github.sparkzxl.mybatis.echo.core.EchoServiceImpl;
import com.github.sparkzxl.mybatis.echo.core.LoadService;
import com.github.sparkzxl.mybatis.echo.properties.EchoProperties;
import com.github.sparkzxl.mybatis.echo.typehandler.RemoteDataTypeHandler;
import com.github.sparkzxl.mybatis.plugins.EchoDataInterceptor;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 回显自动装配
 *
 * @author zhouxinlei
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(EchoProperties.class)
public class EchoAutoConfiguration {

    private final EchoProperties echoProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = EchoProperties.DATA_ECHO_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public EchoServiceImpl echoService(Map<String, LoadService> loadServiceMap) {
        return new EchoServiceImpl(echoProperties, loadServiceMap);
    }

    @Bean
    public EchoDataInterceptor echoResultInterceptor(ApplicationContext applicationContext, EchoProperties echoProperties) {
        return new EchoDataInterceptor(applicationContext, echoProperties);
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
