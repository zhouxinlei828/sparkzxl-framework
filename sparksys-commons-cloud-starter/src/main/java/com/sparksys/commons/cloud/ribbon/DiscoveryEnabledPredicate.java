package com.sparksys.commons.cloud.ribbon;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import org.springframework.lang.Nullable;
/**
 * description: 
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 16:16:06
*/
public abstract class DiscoveryEnabledPredicate extends AbstractServerPredicate {

    @Override
    public boolean apply(@Nullable PredicateKey input) {
        //由于nacosServer继承了Ribbon的Server,那么扩展成其他配置中心同理
        return input != null
                && input.getServer() instanceof NacosServer
                && apply((NacosServer) input.getServer());
    }

    protected abstract boolean apply(NacosServer nacosServer);
}
