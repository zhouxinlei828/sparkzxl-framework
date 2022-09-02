package com.github.sparkzxl.data.sync.admin;

import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.data.sync.admin.config.DataSyncProviderProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/**
 * description: Application启动后运行
 * 日志打印微服务关键配置信息(服务名、接口文档、访问地址)
 *
 * @author zhouxinlei
 */
@Slf4j
public class ProviderStartRunner implements ApplicationRunner, Ordered {

    private final ApplicationContext applicationContext;

    public ProviderStartRunner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        DataSyncProviderProperties dataSyncProviderProperties = applicationContext.getBean(DataSyncProviderProperties.class);
        log.info("\n______________________________________________________________\n\t" +
                        "data.sync is running! \n\t" +
                        "data.sync type is {} \n" +
                        "______________________________________________________________",
                dataSyncProviderProperties.getType());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.APPLICATION_LOG_ORDER.getOrder();
    }
}
