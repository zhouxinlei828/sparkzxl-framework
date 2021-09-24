package com.github.sparkzxl.job.config;

import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.job.properties.XxlExecutorProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * description: xxl-job Executor 自动配置
 *
 * @author zhouxinlei
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(XxlExecutorProperties.class)
@Slf4j
public class XxlJobAutoConfiguration {

    /**
     * 服务名称 包含 XXL_JOB_ADMIN 则说明是 Admin
     */
    private static final String XXL_JOB_ADMIN = "xxl-job-admin";

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor(XxlExecutorProperties xxlExecutorProperties, Environment environment) {
        log.info("xxl-job config init XxlJobProperties：[{}]", JSONUtil.toJsonPrettyStr(xxlExecutorProperties));
// 应用名默认为服务名
        String appName = xxlExecutorProperties.getAppName();
        if (!StringUtils.hasText(appName)) {
            appName = environment.getProperty("spring.application.name");
        }
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlExecutorProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAddress(xxlExecutorProperties.getAddress());
        xxlJobSpringExecutor.setIp(xxlExecutorProperties.getIp());
        xxlJobSpringExecutor.setPort(xxlExecutorProperties.getPort());
        xxlJobSpringExecutor.setAccessToken(xxlExecutorProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlExecutorProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlExecutorProperties.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }


}
