# 链路追踪实战之skywalking应用搭建

## 1. SkyWalking搭建流程

### 1.1 安装环境

|软件环境|简介|版本|RAM
|-------|-------|-------|-------|
|centos|Linux操作系统|7.8|8G|
|docker|docker虚拟化环境|20.10.6|8G|
|docker-compose|docker容器编排|1.29.1|8G|

### 1.2 安装SkyWalking

1. 新建SkyWalking持久化volume目录

```shell
mkdir skywalking
cd skywalking
touch docker-compose.yaml
vim docker-compose.yaml
```

2. 配置docker-compose.yaml文件

```yaml
version: '3.3'
services:
  oap:
    image: apache/skywalking-oap-server:8.4.0-es7
    container_name: oap
    restart: always
    ports:
      - 11800:11800
      - 12800:12800
    environment:
      SW_STORAGE: elasticsearch7
      SW_STORAGE_ES_CLUSTER_NODES: 127.0.0.1:9200
  skywalking-ui:
    image: apache/skywalking-ui
    container_name: skywalking-ui
    depends_on:
      - oap
    links:
      - oap
    restart: always
    ports:
      - 8605:8080
    environment:
      SW_OAP_ADDRESS: oap:12800
      collector.ribbon.listOfServers: oap:12800
      security.user.admin.password: 123456
```

**注意事项**

- SW_STORAGE: skywalking持久化方式，这边选择elasticsearch7版本
- SW_STORAGE_ES_CLUSTER_NODES: elasticsearch连接地址
- SW_OAP_ADDRESS: skywalking-oap-server服务地址

3. 运行docker-compose文件

```shell
docker-compose up -d
```

看见done，说明安装完成

4. 如下图所示，则说明skywalking安装成功

![skywalking-ui.png](../images/skywalking-ui.png)
等待一会

### 1.2 spring-boot接入skywalking

[链路追踪实战之spring-boot接入skywalking](forward/链路追踪实战之spring-boot接入skywalking.md)

