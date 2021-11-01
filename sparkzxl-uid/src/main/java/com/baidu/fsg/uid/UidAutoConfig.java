package com.baidu.fsg.uid;

import com.baidu.fsg.uid.worker.DisposableWorkerIdAssigner;
import com.baidu.fsg.uid.worker.WorkerIdAssigner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 全局id自动配置
 *
 * @author zhouxinlei
 * @date 2021-11-01 09:50:06
 */
@Configuration
public class UidAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public WorkerIdAssigner workerIdAssigner() {
        return new DisposableWorkerIdAssigner((workerNodeEntity) -> {
        });
    }
}
