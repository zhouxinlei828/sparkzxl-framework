# 分布式架构之Nacos注册&配置中心搭建

## 1. Nacos服务搭建流程

> [nacos中文文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)

### 1.1 简介

欢迎来到 Nacos 的世界！

Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。

Nacos 帮助您更敏捷和容易地构建、交付和管理微服务平台。 Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施

### 1.2 安装环境

> 本文基于docker环境安装nacos

|软件环境|简介|版本|RAM
|-------|-------|-------|-------|
|centos|Linux操作系统|7.8|8G|
|docker|docker虚拟化环境|20.10.6|8G|
|docker-compose|docker容器编排|1.29.1|8G|

1. 下载nacos

- Clone 项目

```bash
git clone https://github.com/nacos-group/nacos-docker.git
cd nacos-docker
```

- 单机模式 Derby

```bash
docker-compose -f example/standalone-derby.yaml up
```

- 单机模式 MySQL

如果希望使用MySQL5.7

```bash
docker-compose -f example/standalone-mysql-5.7.yaml up
```

如果希望使用MySQL8

```bash
docker-compose -f example/standalone-mysql-8.yaml up
```

- 集群模式

```bash
docker-compose -f example/cluster-hostname.yaml up
```

2. Nacos 控制台

浏览器中，打开 http://127.0.0.1:8848/nacos ，进行登录：

- 账号：nacos
- 密码：nacos

登录成功后，我们可以看到如下界面：

![nacos-console.png](https://oss.sparksys.top/sparkzxl-component/nacos-console.png)

## 2. 集成到 Spring Cloud

- [Spring Cloud Alibaba 注册中心 Nacos 入门](forward/分布式架构之SpringCloudAlibaba注册中心Nacos入门.md)
- [Spring Cloud Alibaba 配置中心 Nacos 入门](forward/分布式架构之SpringCloudAlibaba配置中心Nacos入门.md)

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
