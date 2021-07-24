# sparkzxl-boot-starter

> 职能：
> 聚合开发脚手架的基本功能，包含spring boot基本环境，工具类，database，日志，API接口文档，web层以及高性能的http调用框架retrofit

## POM

```xml

<dependencies>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-database-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-log-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-swagger-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-web-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.lianjiatech</groupId>
        <artifactId>retrofit-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

## 功能

- 聚合企业级开发脚手架基本的组件
- 启动后的初始化操作，例如，操作系统，依赖环境，访问地址，接口文档地址等
- EnableSpringUtil注解的自动装载
- 高性能的http调用框架retrofit，使用文档：[retrofit-spring-boot-starter](https://github.com/LianjiaTech/retrofit-spring-boot-starter)

## 更多

> 减少搭建框架的时间，让开发人员专注于业务系统的开发

## 使用方法

1. 引入依赖

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-boot-starter</artifactId>
    <version>${sparkzxl.version}</version>
</dependency>
```

2. 继承SparkBootApplication

```java

@SpringBootApplication(scanBasePackages = {"com.github.sparkzxl.authority"})
public class AuthorityApplication extends SparkBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorityApplication.class, args);
    }
}
```

截图示例：

![sparkzxl-boot-screenshots](https://oss.sparksys.top/sparkzxl-component/sparkzxl-boot-screenshots.png)

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
