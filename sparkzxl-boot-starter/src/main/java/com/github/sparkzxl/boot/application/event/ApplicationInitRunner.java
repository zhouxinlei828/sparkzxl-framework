package com.github.sparkzxl.boot.application.event;

import cn.hutool.system.SystemUtil;
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
 * @date 2020-06-05 20:31:48
 */
@Slf4j
public class ApplicationInitRunner implements ApplicationRunner, Ordered {

    private final ApplicationContext applicationContext;

    public ApplicationInitRunner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Environment env = applicationContext.getEnvironment();
        String contextPath = env.getProperty("server.servlet.context-path") == null ? "" : env.getProperty("server.servlet.context-path");
        log.info("\n______________________________________________________________\n\t" +
                        "Java Version: {} \n\t" +
                        "运行系统: {} \n\t" +
                        "Application: {} is running! \n\t" +
                        "访问连接: http://{}:{}/{}\n\t" +
                        "API接口文档：http://{}:{}/doc.html\n" +
                        "______________________________________________________________",
                SystemUtil.getJavaInfo().getVersion(),
                SystemUtil.getOsInfo().getName(),
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                contextPath,
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }
}
