package com.github.sparkzxl.data.sync.admin.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * description: zookeeper注入条件判断类
 *
 * @author zhouxinlei
 * @since 2022-06-21 15:50:42
 */
public class ZookeeperCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        final Environment environment = conditionContext.getEnvironment();
        return environment.containsProperty("spring.coordinate.zookeeper.zkServers")
                || environment.containsProperty("spring.coordinate.zookeeper.zk-servers");
    }
}
