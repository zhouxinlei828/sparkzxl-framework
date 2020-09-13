package com.github.sparkzxl.job.config;

import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.job.properties.XxlJobProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: xxl-job Executor 自动配置
 *
 * @author: zhouxinlei
 * @date: 2020-08-21 14:13:13
 */
@Configuration
@EnableConfigurationProperties(XxlJobProperties.class)
@Slf4j
public class XxlJobConfig {

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor(XxlJobProperties xxlJobProperties) {
        log.info(">>>>>>>>>>> xxl-job config init XxlJobProperties：{}", JSONUtil.toJsonPrettyStr(xxlJobProperties));
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(xxlJobProperties.getAppName());
        xxlJobSpringExecutor.setAddress(xxlJobProperties.getAddress());
        xxlJobSpringExecutor.setIp(xxlJobProperties.getIp());
        xxlJobSpringExecutor.setPort(xxlJobProperties.getPort());
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }


}
