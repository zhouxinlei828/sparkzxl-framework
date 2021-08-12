# sparkzxl-component框架使用手册

## 1. spring boot框架搭建步骤

> 本脚手架涉及功能组件比较多，建议使用maven私库nexus

### 1.1 在idea新建maven项目

![idea-new-project](https://oss.sparksys.top/sparkzxl-component/idea-new-project.png)

### 1.2 pom定义

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-auth</artifactId>
    <version>1.0</version>
    <packaging>pom</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <!-- maven -->
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.target>${java.version}</maven.compiler.target>
        <maven-compiler-plugin.version>3.8.0</maven-compiler-plugin.version>
        <maven-resources-plugin.version>3.1.0</maven-resources-plugin.version>
        <maven-deploy-plugin.version>3.0.0-M1</maven-deploy-plugin.version>
        <jib-maven-plugin.version>2.5.0</jib-maven-plugin.version>
        <maven-source-plugin.version>3.1.0</maven-source-plugin.version>
        <maven-javadoc-plugin.version>3.0.0</maven-javadoc-plugin.version>
        <spring-boot-maven.version>2.3.9.RELEASE</spring-boot-maven.version>
        <mapstruct.version>1.4.0.CR1</mapstruct.version>
        <sparkzxl-dependencies.version>1.0.1.RELEASE</sparkzxl-dependencies.version>
        <lombok.version>1.18.8</lombok.version>
        <mybatis-spring-boot-starter.version>2.1.4</mybatis-spring-boot-starter.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>
    </dependencies>
    <dependencyManagement>
        <dependencies>
            <!-- sparkzxl-dependencies 依赖-->
            <dependency>
                <groupId>com.github.sparkzxl</groupId>
                <artifactId>sparkzxl-dependencies</artifactId>
                <version>${sparkzxl-dependencies.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency
        </dependencies>
    </dependencyManagement>

    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
        </resources>

        <pluginManagement>
            <plugins>
                <!-- 提供给 mapstruct 使用 -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>${maven-compiler-plugin.version}</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.mapstruct</groupId>
                                <artifactId>mapstruct-processor</artifactId>
                                <version>${mapstruct.version}</version>
                            </path>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                        </annotationProcessorPaths>
                        <compilerArgs>
                            <arg>
                                -Amapstruct.verbose=true
                            </arg>
                        </compilerArgs>
                    </configuration>
                </plugin>
                <!-- resources资源插件 -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-resources-plugin</artifactId>
                    <version>${maven-resources-plugin.version}</version>
                    <configuration>
                        <delimiters>
                            <delimiter>@</delimiter>
                        </delimiters>
                        <useDefaultDelimiters>false</useDefaultDelimiters>
                        <encoding>UTF-8</encoding>
                        <!-- 后缀为pem、pfx的证书文件 -->
                        <nonFilteredFileExtensions>
                            <nonFilteredFileExtension>pem</nonFilteredFileExtension>
                            <nonFilteredFileExtension>pfx</nonFilteredFileExtension>
                            <nonFilteredFileExtension>p12</nonFilteredFileExtension>
                            <nonFilteredFileExtension>key</nonFilteredFileExtension>
                            <nonFilteredFileExtension>jks</nonFilteredFileExtension>
                            <nonFilteredFileExtension>db</nonFilteredFileExtension>
                            <nonFilteredFileExtension>txt</nonFilteredFileExtension>
                        </nonFilteredFileExtensions>
                    </configuration>
                </plugin>
                <!-- java文档插件 -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-javadoc-plugin</artifactId>
                    <version>${maven-javadoc-plugin.version}</version>
                    <configuration>
                        <encoding>UTF-8</encoding>
                        <!--<aggregate>true</aggregate>-->
                        <charset>UTF-8</charset>
                        <docencoding>UTF-8</docencoding>
                    </configuration>
                    <executions>
                        <execution>
                            <id>attach-javadocs</id>
                            <goals>
                                <goal>jar</goal>
                            </goals>
                            <configuration>
                                <additionalJOption>-Xdoclint:none</additionalJOption>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
                <!--配置生成源码包 -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-source-plugin</artifactId>
                    <version>${maven-source-plugin.version}</version>
                    <executions>
                        <execution>
                            <id>attach-sources</id>
                            <goals>
                                <goal>jar</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-deploy-plugin</artifactId>
                    <version>${maven-deploy-plugin.version}</version>
                </plugin>
                <!-- 打包 -->
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot-maven.version}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>repackage</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>com.google.cloud.tools</groupId>
                    <artifactId>jib-maven-plugin</artifactId>
                    <version>${jib-maven-plugin.version}</version>
                    <configuration>
                        <from>
                            <image>registry.cn-hangzhou.aliyuncs.com/sparkzxl/java:8.1</image>
                        </from>
                        <to>
                            <image>registry.cn-hangzhou.aliyuncs.com/sparkzxl/${project.artifactId}</image>
                            <tags>
                                <tag>${project.version}</tag>
                            </tags>
                        </to>
                        <container>
                            <jvmFlags>
                                <jvmFlag>-javaagent:/skywalking/agent/skywalking-agent.jar</jvmFlag>
                                <jvmFlag>-Xms512m</jvmFlag>
                                <jvmFlag>-Xmx512m</jvmFlag>
                            </jvmFlags>
                            <appRoot>/home/${project.artifactId}</appRoot>
                            <workingDirectory>/home/${project.artifactId}</workingDirectory>
                            <creationTime>USE_CURRENT_TIMESTAMP</creationTime>
                            <environment>
                                <SW_AGENT_NAME>${project.artifactId}</SW_AGENT_NAME>
                                <SW_AGENT_COLLECTOR_BACKEND_SERVICES>172.34.200.5:11800
                                </SW_AGENT_COLLECTOR_BACKEND_SERVICES>
                                <SW_AGENT_SPAN_LIMIT>2000</SW_AGENT_SPAN_LIMIT>
                                <TZ>Asia/Shanghai</TZ>
                            </environment>
                            <labels>
                                <name>${project.artifactId}</name>
                            </labels>
                        </container>
                        <allowInsecureRegistries>true</allowInsecureRegistries>
                    </configuration>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>build</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
            </plugin>
            <!-- resources资源插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
            </plugin>
            <!-- java文档插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
            </plugin>
            <!--配置生成源码包 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

```text
repositories:主要设置你的maven私库地址，你也可以不用，将sparkzxl-component 通过mvn install安装到本地即可，具体用法可参考下文链接
jib-maven-plugin:maven 打包应用代码为docker images 插件，
使用方法可见链接
```

- [maven settings 私服配置](https://blog.csdn.net/k21325/article/details/106761582)
- [谷歌mavenjib插件](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)

### 1.3 引入组件依赖

通过idea的自动提示，引入组件库，例如引入sparkzxl-boot-starter

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-boot-starter</artifactId>
</dependency>
```

截图示例：

![import-maven](https://oss.sparksys.top/sparkzxl-component/import-maven.png)

### 1.4 组件自动装配

> 引入对应的starter，在启动的时候，spring boot就会自动装配启动
> 原理就是spring boot提供的spring.factories里面加入你需要在启动的时候需要装载的类

### 1.5 yaml自动提示功能

在每个starter都会有一个properties文件，里面就是yaml可选的属性配置

### 1.6 框架默认需要启动的starter

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-boot-starter</artifactId>
</dependency>
<dependency>
<groupId>com.github.sparkzxl</groupId>
<artifactId>sparkzxl-jwt-starter</artifactId>
</dependency>
<dependency>
<groupId>com.github.sparkzxl</groupId>
<artifactId>sparkzxl-cache-starter</artifactId>
</dependency>
```

**注意事项**
在sparkzxl-boot-starter里面默认加载了sparkzxl-core(工具类)，sparkzxl-database-starter(mybatis-plus框架)，sparkzxl-log-starter(日志框架)
sparkzxl-swagger-starter(swagger文档)等maven依赖

## 2 配置启动

### 2.1 必须配置的参数

```yaml
server:
  shutdown: graceful
  port: 8085
  undertow:
    buffer-size: 1024
    direct-buffers: on
    always-set-keep-alive: true
  error:
    include-exception: true
    include-stacktrace: ALWAYS
    include-message: ALWAYS
  compression:
    enabled: true
    min-response-size: 1024
    mime-types: application/javascript,application/json,application/xml,text/html,text/xml,text/plain,text/css,image/*
spring:
  main:
    allow-bean-definition-overriding: true
  application:
    name: demo-server
  datasource:
    driver-class-name: com.p6spy.engine.spy.P6SpyDriver
    url: jdbc:p6spy:mysql://127.0.0.1:3306/demo?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456
  redis:
    host: 127.0.0.1
    port: 6379
    time-out: 28800
    database: 0
    password: ''
    jedis:
      pool:
        max-active: 20
        max-wait: 200
        min-idle: 0
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  type-aliases-package: org.example.demo.infrastructure.entity
  custom:
    mapper-scan: org.example.demo.infrastructure.mapper
    injection:
      enabled: true
      aop-enabled: true
  type-enums-package: org.example.demo.infrastructure.enums
  configuration:
    map-underscore-to-camel-case: true
knife4j:
  enable: true
  description: demo 在线文档
  base-package: org.example.demo.interfaces
  group: demo应用
  title: demo 在线文档
  terms-of-service-url: https://www.sparksys.top
  version: 1.0
  license: Powered By farben
  license-url: https://github.com/farben
  contact:
    name: zhouxinlei
    email: zhouxinlei298@163.com
    url: https://github.com/sparkzxl
```

### 2.2 新建启动类

```java
package org.example.demo;

import com.github.sparkzxl.boot.SparkBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * description: 启动类
 *
 * @author charles.zhou
 * @since 2021-02-01 11:18:40
 */
@SpringBootApplication(scanBasePackages = {"org.example.demo"})
@Slf4j
public class DemoApplication extends SparkBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

```

## 3 spring cloud 搭建

> 在上面步骤spring boot上的基础加入spring cloud

### 3.1 修改pom文件

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-distributed-cloud-starter</artifactId>
</dependency>
```

**注意事项：** sparkzxl-distributed-cloud-starter里面依赖了nacos配置中心和nacos注册中心的maven依赖，还有openfeign服务之间的调用，熔断器使用hystrix

### 3.2 bootstrap.yaml配置

> - bootstrap-dev.yaml
> - bootstrap-test.yaml
> - bootstrap-prod.yaml

以上各自加上以下spring cloud配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        namespace: 65c07321-73fa-43f5-bbea-30ac6a4e6021
      config:
        server-addr: 127.0.0.1:8848
        namespace: 65c07321-73fa-43f5-bbea-30ac6a4e6021
        file-extension: yaml
        prefix: ${spring.application.name}
        enable-remote-sync-config: true
        refresh-enabled: true
# feign，hystrix相关
feign:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
  hystrix:
    enabled: true   # feign 熔断机制是否开启
    #支持压缩的mime types
  compression: # 请求压缩
    request:
      enabled: true
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response: # 响应压缩
      enabled: true
ribbon:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
  ReadTimeout: 150000
  ConnectTimeout: 150000  # [ribbon超时时间]大于[熔断超时],那么会先走熔断，相当于你配的ribbon超时就不生效了  ribbon和hystrix是同时生效的，哪个值小哪个生效
  MaxAutoRetries: 1             # 最大自动重试
  MaxAutoRetriesNextServer: 1   # 最大自动像下一个服务重试
  NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule
  OkToRetryOnAllOperations: false  #无论是请求超时或者socket read timeout都进行重试
hystrix:
  threadpool:
    default:
      coreSize: 1000 # #并发执行的最大线程数，默认10
      maxQueueSize: 1000 # #BlockingQueue的最大队列数
      queueSizeRejectionThreshold: 500 # #即使maxQueueSize没有达到，达到queueSizeRejectionThreshold该值后，请求也会被拒绝
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 150000  # 熔断超时 ribbon和hystrix是同时生效的，哪个值小哪个生效
```

## 4 DDD领域驱动设计的落地

### 4.1 DDD领域驱动介绍

> DDD（Domain-Driven Design 领域驱动设计）是由Eric Evans最先提出，目的是对软件所涉及到的领域进行建模，以应对系统规模过大时引起的软件复杂性的问题。整个过程大概是这样的，开发团队和领域专家一起通过 通用语言(Ubiquitous Language)去理解和消化领域知识，从领域知识中提取和划分为一个一个的子领域（核心子域，通用子域，支撑子域），并在子领域上建立模型，再重复以上步骤，这样周而复始，构建出一套符合当前领域的模型。

- [1.初识领域驱动设计DDD落地](https://bugstack.cn/itstack-demo-ddd/2019/10/15/DDD%E4%B8%93%E9%A2%98%E6%A1%88%E4%BE%8B%E4%B8%80-%E5%88%9D%E8%AF%86%E9%A2%86%E5%9F%9F%E9%A9%B1%E5%8A%A8%E8%AE%BE%E8%AE%A1DDD%E8%90%BD%E5%9C%B0.html)
- [2.领域层决策规则树服务设计](https://bugstack.cn/itstack-demo-ddd/2019/10/16/DDD%E4%B8%93%E9%A2%98%E6%A1%88%E4%BE%8B%E4%BA%8C-%E9%A2%86%E5%9F%9F%E5%B1%82%E5%86%B3%E7%AD%96%E8%A7%84%E5%88%99%E6%A0%91%E6%9C%8D%E5%8A%A1%E8%AE%BE%E8%AE%A1.html)
- [3.领域驱动设计架构基于SpringCloud搭建微服务](https://bugstack.cn/itstack-demo-ddd/2019/10/17/DDD%E4%B8%93%E9%A2%98%E6%A1%88%E4%BE%8B%E4%B8%89-%E9%A2%86%E5%9F%9F%E9%A9%B1%E5%8A%A8%E8%AE%BE%E8%AE%A1%E6%9E%B6%E6%9E%84%E5%9F%BA%E4%BA%8ESpringCloud%E6%90%AD%E5%BB%BA%E5%BE%AE%E6%9C%8D%E5%8A%A1.html)

## 5. 示例项目

- [sparkzxl-demo示例项目](https://gitee.com/AbsolutelyNT/sparkzxl-demo.git)
- [sparkzxl-auth单点登陆](https://gitee.com/AbsolutelyNT/sparkzxl-auth.git)

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
