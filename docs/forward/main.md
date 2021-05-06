# sparkzxl-component

## 简介

> sparkzxl-component包含springboot项目的封装，主要功能是统一了对外接口的api访问格式，web模块进行了封装，基于DDD领域驱动模型设计代码，具体落地实施，对常用的工具类包进行封装，简单易用，elasticsearch，mybatis组件。集成了oauth2，redis缓存，本地缓存的构建，分布式锁的封装等等，是快速开发的脚手架，简易适用于企业级框架搭建

## 项目地址

[sparkzxl-component](https://github.com/sparkzxl/sparkzxl-component.git)

**落地最佳实践**：

- [sparkzxl-cloud](https://github.com/sparkzxl/sparkzxl-cloud.git)
- [sparkzxl-auth](https://github.com/sparkzxl/sparkzxl-auth.git)

## 文档更新记录

- [组件框架搭建/框架搭建手册](forward/框架搭建手册.md)
- [应用部署实践/1.Jenkins实战之流水线](forward/Jenkins实战之流水线.md)
- [应用部署实践/2.Jenkins实战之流水线语法详解](forward/Jenkins实战之流水线语法详解.md)
- [应用部署实践/3.Jenkins实战之流水线应用部署](forward/Jenkins实战之流水线应用部署.md)
- [应用部署实践/4.链路追踪实战之skywalking应用搭建](forward/链路追踪实战之skywalking应用搭建.md)
- [组件功能介绍/sparkzxl-boot-starter组件](forward/sparkzxl-boot.md)
- [组件功能介绍/sparkzxl-cache-starter组件](forward/sparkzxl-cache.md)
- [组件功能介绍/sparkzxl-core组件](forward/sparkzxl-core.md)
- [组件功能介绍/sparkzxl-database-starter组件](forward/sparkzxl-database.md)
- [组件功能介绍/sparkzxl-user-starter组件](forward/sparkzxl-user.md)
- [组件功能介绍/sparkzxl-web-starter组件](forward/sparkzxl-web.md)

## 组件简介

```Text
sparkzxl-component                               -- 核心组件模块
├── sparkzxl-boot-starter                         -- sparkzxl boot引导
├── sparkzxl-cache-starter                        -- cache组件封装
├── sparkzxl-core                                 -- 工具类组件
├── sparkzxl-database-starter                     -- 持久层组件
├── sparkzxl-distributed		          -- spring cloud组件
├──── sparkzxl-distributed-cloud-starter            -- spring cloud starter
├── sparkzxl-elasticsearch-starter                -- 搜索引擎组件
├── sparkzxl-job-executor-starter                 -- 分布式job组件
├── sparkzxl-jwt-starter                          -- JWT组件
├── sparkzxl-log-starter                          -- 日志log组件
├── sparkzxl-mail-starter                         -- 邮件组件
├── sparkzxl-oauth2-resource-starter	          -- oauth2资源保护组件
├── sparkzxl-oauth2-server-starter                -- oauth2授权组件
├── sparkzxl-oss-starter                          -- 对象存储（兼容阿里云oss，minio等）组件
├── sparkzxl-patterns-starter                     -- 设计模式组件
├── sparkzxl-redisson-starter                     -- redisson分布式锁组件
├── sparkzxl-security-starter                     -- 权限控制组件
├── sparkzxl-swagger-starter                      -- API文档组件
├── sparkzxl-user-starter                         -- 全局用户绑定组件
├── sparkzxl-web-starter                          -- web视图层组件
├── sparkzxl-zookeeper-starter                    -- zookeeper组件
```

## 组件功能

- [sparkzxl-boot-starter组件](forward/sparkzxl-boot.md)
- [sparkzxl-cache-starter组件](forward/sparkzxl-cache.md)
- [sparkzxl-core组件](forward/sparkzxl-core.md)
- [sparkzxl-database-starter组件](forward/sparkzxl-database.md)
- [sparkzxl-user-starter组件](forward/sparkzxl-user.md)
- [sparkzxl-web-starter组件](forward/sparkzxl-web.md)
