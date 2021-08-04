# sparkzxl-component学习教程

<p>
<a href="https://search.maven.org/search?q=sparkzxl"><img src="https://img.shields.io/badge/sparkzxl--component-1.3-blue" alt="maven"></a>
<a href="https://www.apache.org/licenses/"><img src="https://img.shields.io/badge/license-Apache%202.0-blue" alt="Apache 2.0"></a>
<a href="https://github.com/sparkzxl/sparkzxl-component"><img src="https://img.shields.io/badge/组件-sparkzxl--component-orange" alt="sparkzxl-component"></a>
<a href="https://github.com/sparkzxl/sparkzxl-auth"><img src="https://img.shields.io/badge/分布式认证-sparkzxl--auth-success" alt="分布式认证"></a>
</p>

[更新日志](forward/CHANGELOG.md)

## 简介

> sparkzxl-component包含springboot项目的封装，主要功能是统一了对外接口的api访问格式，web模块进行了封装，基于DDD领域驱动模型设计代码，具体落地实施，对常用的工具类包进行封装，简单易用，elasticsearch，mybatis组件。集成了oauth2，redis缓存，本地缓存的构建，分布式锁的封装等等，是快速开发的脚手架，简易适用于企业级框架搭建

## 项目地址

[sparkzxl-component](https://github.com/sparkzxl/sparkzxl-component.git)

**落地最佳实践**：

- [sparkzxl-cloud](https://github.com/sparkzxl/sparkzxl-cloud.git)
- [sparkzxl-auth](https://github.com/sparkzxl/sparkzxl-auth.git)

## 在线体验

- [spark auth](https://auth.sparksys.top)

!> 账户：test 密码：123456

## 演示效果

![sparkzxl-demo-7.png](https://oss.sparksys.top/images/sparkzxl-demo-7.png)

![sparkzxl-demo-6.png](https://oss.sparksys.top/images/sparkzxl-demo-6.png)

![sparkzxl-demo-5.png](https://oss.sparksys.top/images/sparkzxl-demo-5.png)

![sparkzxl-demo-4.png](https://oss.sparksys.top/images/sparkzxl-demo-4.png)

![sparkzxl-demo-3.png](https://oss.sparksys.top/images/sparkzxl-demo-3.png)

![sparkzxl-demo-2.png](https://oss.sparksys.top/images/sparkzxl-demo-2.png)

![sparkzxl-demo-1.png](https://oss.sparksys.top/images/sparkzxl-demo-1.png)

## 开源博客

- [凛冬王昭君的笔记](https://www.sparksys.top)

## 核心架构

![分布式系统架构](https://oss.sparksys.top/sparkzxl-component/distributed-architecture.jpg)

## 未来展望

## 组件包依赖下载指引

> 组件jar包已上传maven中央仓库，可进入[maven中央仓库](https://search.maven.org/) 搜索下载

![nexus-compoment.png](https://oss.sparksys.top/sparkzxl-component/nexus-compoment.png)

## 组件框架搭建

- [框架搭建手册之maven私库nexus实战](forward/framework/框架搭建手册之maven私库nexus实战.md)
- [框架搭建手册之idea搭建代码环境](forward/framework/框架搭建手册之idea搭建代码环境.md)

## 分布式架构篇

- [Nacos注册&配置中心搭建](forward/distributed/分布式架构之Nacos注册&配置中心搭建.md)
- [Spring Cloud Alibaba 注册中心 Nacos 入门](forward/distributed/分布式架构之SpringCloudAlibaba注册中心Nacos入门.md)
- [Spring Cloud Alibaba 配置中心 Nacos 入门](forward/distributed/分布式架构之SpringCloudAlibaba配置中心Nacos入门.md)
- [Spring Cloud openfeign 服务调用](forward/222)
- [Spring Cloud Openfeign 异常信息传递](forward/distributed/SpringCloudOpenfeign异常信息传递.md)
- [Spring Cloud Ribbon 服务负载均衡](forward/222)
- [Spring Cloud hystrix 服务容错保护](forward/222)
- [Spring Cloud Gateway API网关服务](forward/222)
- [Spring Boot Admin 微服务应用监控](forward/222)
- [Spring Cloud Security 授权认证](forward/222)
- [Spring Cloud Security：Oauth2使用入门](forward/222)
- [Spring Cloud Alibaba Sentinel实现熔断与限流](forward/222)
- [Spring Cloud Alibaba Seata 分布式事务问题](forward/222)
- [Spring-cloud-gateway-oauth2 实现统一认证和鉴权](forward/222)
- [Spring Cloud Skywalking链路追踪](forward/222)
- [Spring Cloud 微服务聚合swagger文档](forward/222)
- [我用AmazonS3解决了众多云厂商oss的痛点](forward/distributed/我用AmazonS3解决了众多云厂商oss的痛点.md)
- [ELK+Filebeat+Kafka分布式日志管理平台搭建](forward/distributed/分布式架构之ELK+Filebeat+Kafka分布式日志管理平台搭建.md)
- [高吞吐量的分布式消息中间件kafka初体验](forward/222.md)

## 应用部署实践

- [Docker环境部署安装](forward/deploy/Docker环境部署安装.md)
- [Jenkins+Docker+Gitlab+Harbor服务器部署](forward/deploy/Jenkins+Docker+Gitlab+Harbor服务器部署.md)
- [Jenkins全自动化部署SpringBoot项目](forward/deploy/Jenkins全自动化部署SpringBoot项目.md)
- [Jenkins打包并远程部署NodeJS应用](forward/deploy/Jenkins打包并远程部署NodeJS应用.md)
- [Jenkins实战之流水线](forward/deploy/Jenkins实战之流水线.md)
- [Jenkins实战之流水线语法详解](forward/deploy/Jenkins实战之流水线语法详解.md)
- [Jenkins实战之流水线应用部署](forward/deploy/Jenkins实战之流水线应用部署.md)
- [链路追踪实战之SkyWalking环境搭建](forward/distributed/链路追踪实战之SkyWalking环境搭建.md)
- [链路追踪实战之JDK镜像制作](forward/distributed/链路追踪实战之JDK镜像制作.md)

## 组件功能介绍

- [sparkzxl-boot-starter组件](forward/component/sparkzxl-boot.md)
- [sparkzxl-cache-starter组件](forward/component/sparkzxl-cache.md)
- [sparkzxl-core组件](forward/component/sparkzxl-core.md)
- [sparkzxl-database-starter组件](forward/component/sparkzxl-database.md)
- [sparkzxl-user-starter组件](forward/component/sparkzxl-user.md)
- [sparkzxl-web-starter组件](forward/component/sparkzxl-web.md)

## 文档更新手册

- [文档更新手册](forward/文档更新手册.md)

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
