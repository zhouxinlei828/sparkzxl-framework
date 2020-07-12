# sparksys-commons
对spring-boot的高度封装，数据层，web层，log日志，工具类的统一集成组件，适用于分布式组件的低耦合集成

### 组织结构
> 主要是统一了对外接口的api访问格式，web模块进行了封装，基于DDD领域驱动模型设计代码，具体落地实施，对常用的core包进行二次封装，简单易用，elasticsearch，mybatis组件。集成了oauth2，redis缓存，本地缓存的构建，分布式锁的封装等等

```text
sparksys-commons                               -- 核心组件模块
├── sparksys-commons-activiti-starter             -- activiti组件封装
├── sparksys-commons-cache-starter                -- cache组件封装
├── sparksys-commons-core                         -- 工具类组件
├── sparksys-commons-database-starter             -- mybatis组件封装
├── sparksys-commons-elasticsearch-starter        -- 搜索引擎组件封装
├── sparksys-commons-log-starter                  -- 日志log组件封装
├── sparksys-commons-mail-starter                 -- 邮件组件封装
├── sparksys-commons-oauth2-starter               -- oauth2平台授权组件封装
├── sparksys-commons-security-starter             -- 权限框架组件封装
├── sparksys-commons-swagger-starter              -- swagger组件封装
├── sparksys-commons-web-starter                  -- web统一组件封装
├── sparksys-commons-zookeeper-starter            -- zookeeper组件封装
```