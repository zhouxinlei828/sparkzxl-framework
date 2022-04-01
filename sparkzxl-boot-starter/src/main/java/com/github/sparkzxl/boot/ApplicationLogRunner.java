package com.github.sparkzxl.boot;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.system.SystemUtil;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * description: Application启动后运行
 * 日志打印微服务关键配置信息(服务名、接口文档、访问地址)
 *
 * @author zhouxinlei
 */
@Slf4j
public class ApplicationLogRunner implements ApplicationRunner, Ordered {

    private final ApplicationContext applicationContext;

    public ApplicationLogRunner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Environment env = applicationContext.getEnvironment();
        String applicationName = env.getProperty("spring.application.name");
        int port = Convert.toInt(env.getProperty("server.port"), -1);
        String url = UrlBuilder.create()
                .setHost(NetUtil.getLocalhostStr())
                .setPort(port).build();
        log.info("\n______________________________________________________________\n\t" +
                        "Java Version: {} \n\t" +
                        "Operating System: {} \n\t" +
                        "Application: {} \n\t" +
                        "API接口文档：{}doc.html\n\t" +
                        "Health Check Endpoint：{}actuator/health\n" +
                        "______________________________________________________________",
                SystemUtil.getJavaInfo().getVersion(),
                SystemUtil.getOsInfo().getName(),
                applicationName,
                url,
                url);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.APPLICATION_LOG_ORDER.getOrder();
    }
}
