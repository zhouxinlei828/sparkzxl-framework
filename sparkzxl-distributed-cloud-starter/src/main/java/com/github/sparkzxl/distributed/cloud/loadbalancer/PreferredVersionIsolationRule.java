package com.github.sparkzxl.distributed.cloud.loadbalancer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.ribbon.ExtendBalancer;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.distributed.cloud.properties.LoadBalancerRuleProperties;
import com.google.common.collect.Lists;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * description: 版本隔离优先选择负载均衡规则
 * 开发A访问时，请求的所有路由优先调用开发A本机启动的实例，如果没有则调用服务器上的实例
 * 开发B访问时同上，请求的所有路由优先调用开发B本机启动的实例，如果没有则调用服务器上的实例
 *
 * @author zhouxinlei
 * @date 2021-10-21 14:52:49
 */
@Slf4j
public class PreferredVersionIsolationRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Autowired
    private NacosServiceManager nacosServiceManager;
    @Autowired
    private LoadBalancerRuleProperties loadBalancerRuleProperties;

    /**
     * 优先根据版本号取实例
     */
    @Override
    public Server choose(Object key) {
        try {
            String clusterName = this.nacosDiscoveryProperties.getClusterName();
            String group = this.nacosDiscoveryProperties.getGroup();
            DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) this.getLoadBalancer();
            String name = loadBalancer.getName();
            NamingService namingService = this.nacosServiceManager.getNamingService(this.nacosDiscoveryProperties.getNacosProperties());
            List<Instance> instances = namingService.selectInstances(name, group, true);
            if (CollectionUtils.isEmpty(instances)) {
                log.warn("no instance in service {}", name);
                return null;
            } else {
                List<Instance> instancesToChoose = instances;
                if (StringUtils.isNotBlank(clusterName)) {
                    List<Instance> sameClusterInstances = instances.stream().filter((instance) -> Objects.equals(clusterName, instance.getClusterName())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(sameClusterInstances)) {
                        instancesToChoose = sameClusterInstances;
                    } else {
                        log.warn("A cross-cluster call occurs，name = {}, clusterName = {}, instance = {}", name, clusterName, instances);
                    }
                }
                List<Instance> targetInstanceList = Lists.newArrayList();
                String version = RequestContextHolderUtils.getRequest().getHeader(BaseContextConstants.VERSION);
                if (loadBalancerRuleProperties.isEnabled()) {
                    // 判断版本号是否存在
                    if (StringUtils.isNotBlank(version)) {
                        //取指定版本号的实例
                        targetInstanceList = instancesToChoose.stream().filter(instance -> version.equals(instance.getMetadata().get(BaseContextConstants.VERSION)))
                                .collect(Collectors.toList());
                    }
                    if (CollectionUtils.isEmpty(targetInstanceList)) {
                        //只取无版本号的实例
                        targetInstanceList = instancesToChoose.stream().filter(instance -> StringUtils.isEmpty(instance.getMetadata().get(BaseContextConstants.VERSION)))
                                .collect(Collectors.toList());
                    }
                }
                if (CollectionUtils.isEmpty(targetInstanceList)) {
                    // 取所有的实例
                    targetInstanceList = instancesToChoose;
                }
                Instance instance = ExtendBalancer.getHostByRandomWeight2(targetInstanceList);
                log.warn("请求实例 = {}, version = {}, instance = {}", name, version, instance.getIp().concat(":").concat(String.valueOf(instance.getPort())));
                return new NacosServer(instance);

            }
        } catch (Exception e) {
            log.warn("PreferredVersionRule error", e);
            return null;
        }
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }
}
