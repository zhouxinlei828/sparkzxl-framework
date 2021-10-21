package com.github.sparkzxl.distributed.cloud.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.BaseContextHolder;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description: 版本优先选择负载均衡规则
 * 开发A访问时，请求的所有路由优先调用开发A本机启动的实例，如果没有则调用服务器上的实例
 * 开发B访问时同上，请求的所有路由优先调用开发B本机启动的实例，如果没有则调用服务器上的实例
 *
 * @author zhouxinlei
 * @date 2021-10-21 14:52:49
 */
@Slf4j
public class TopChoiceVersionIsolationRule extends RoundRobinRule {

    /**
     * 优先根据版本号取实例
     */
    @Override
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        String version = BaseContextHolder.get(BaseContextConstants.REQUEST_VERSION);
        List<Server> targetList = null;
        List<Server> upList = lb.getReachableServers();
        if (StrUtil.isNotEmpty(version)) {
            //取指定版本号的实例
            targetList = upList.stream().filter(
                    server -> version.equals(
                            ((NacosServer) server).getMetadata().get(BaseContextConstants.REQUEST_VERSION)
                    )
            ).collect(Collectors.toList());
        }

        if (CollUtil.isEmpty(targetList)) {
            //只取无版本号的实例
            targetList = upList.stream().filter(
                    server -> {
                        String metadataVersion = ((NacosServer) server).getMetadata().get(BaseContextConstants.REQUEST_VERSION);
                        return StrUtil.isEmpty(metadataVersion);
                    }
            ).collect(Collectors.toList());
        }

        if (CollUtil.isNotEmpty(targetList)) {
            return getServer(targetList);
        }
        return super.choose(lb, key);
    }

    /**
     * 随机取一个实例
     */
    private Server getServer(List<Server> upList) {
        int nextInt = RandomUtil.randomInt(upList.size());
        Server server = upList.get(nextInt);
        log.info("请求服务实例ip:{}", server.getHostPort());
        return server;
    }
}
