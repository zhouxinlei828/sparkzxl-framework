package com.github.sparkzxl.distributed.cloud.config;

import com.github.sparkzxl.distributed.cloud.loadbalancer.PreferredVersionIsolationRule;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Configuration(proxyBeanMethods = false)
public class GlobalRibbonConfig {

    @Bean
    public IRule ribbonRule() {
        return new PreferredVersionIsolationRule();
    }

    @Bean
    public IPing ribbonPing() {
        return new DummyPing();
    }

    @Bean
    public ServerList<Server> ribbonServerList(IClientConfig config) {
        return new BazServiceList(config);
    }

    @Bean
    public ServerListSubsetFilter serverListFilter() {
        ServerListSubsetFilter filter = new ServerListSubsetFilter();
        return filter;
    }

    public static class BazServiceList extends ConfigurationBasedServerList {

        public BazServiceList(IClientConfig config) {
            super.initWithNiwsConfig(config);
        }

    }

}
