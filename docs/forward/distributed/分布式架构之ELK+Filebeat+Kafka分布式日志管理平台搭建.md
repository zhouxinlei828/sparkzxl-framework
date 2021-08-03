# 分布式架构之ELK+Filebeat+Kafka分布式日志管理平台搭建

## 1 工作流程

> 在这之前，我写了三篇文章关于日志系统平台的搭建，我这边现简单列出这几种的工作流程

### 1.1 ELK

[Docker整合ELK实现日志收集](https://www.sparksys.top/archives/14)

![ELK](https://oss.sparksys.top/halo/ELK_1591159459144.png)

### 1.2 ELFK

[docker 安装ELFK 实现日志统计](https://www.sparksys.top/archives/39)

![ELFK](https://oss.sparksys.top/halo/ELFK_1591159739542.png)

### 1.3 架构演进

- ELK缺点：ELK架构，并且Spring Boot应用使用 logstash-logback-encoder 直接发送给 Logstash，缺点就是Logstash是重量级日志收集server，占用cpu资源高且内存占用比较高
- ELFK缺点：一定程度上解决了ELK中Logstash的不足，但是由于Beats 收集的每秒数据量越来越大，Logstash 可能无法承载这么大量日志的处理

### 1.4 日志新贵ELK + Filebeat + Kafka

随着 Beats 收集的每秒数据量越来越大，Logstash 可能无法承载这么大量日志的处理。虽然说，可以增加 Logstash 节点数量，提高每秒数据的处理速度，但是仍需考虑可能 Elasticsearch
无法承载这么大量的日志的写入。此时，我们可以考虑**引入消息队列**
，进行缓存：

- Beats 收集数据，写入数据到消息队列中。
- Logstash 从消息队列中，读取数据，写入 Elasticsearch 中

如下就是其工作流程
![ELFK_KAFKA.png](https://oss.sparksys.top/halo/ELFK_KAFKA_1594292465338.png)

## 2. ELK + Filebeat + Kafka 分布式日志管理平台搭建

### 2.1 ELFK的搭建

[docker 安装ELFK 实现日志统计](https://www.sparksys.top/archives/39)

#### 2.1.1 Filebeat变动

由于我们架构演变，在filebeat中原来由传输到logstash改变为发送到kafka，我们这边filebeat.yml改动的部分为：

```yaml
filebeat.inputs:
  - type: log
    enabled: true
    paths:
      - /var/logs/springboot/sparkzxl-authorization.log # 配置我们要读取的 Spring Boot 应用的日志
    fields:
      #定义日志来源，添加了自定义字段
      log_source: authorization
  - type: log
    enabled: true
    paths:
      - /var/logs/springboot/sparkzxl-gateway.log
    fields:
      log_source: gateway
  - type: log
    enabled: true
    paths:
      - /var/logs/springboot/sparkzxl-file.log
    fields:
      log_source: file
  - type: log
    enabled: true
    paths:
      - /var/logs/springboot/sparkzxl-auth-server.log
    fields:
      log_source: oauth
    #================================ Outputs =====================================
    #-------------------------- Elasticsearch output ------------------------------
    #output.elasticsearch:
    # Array of hosts to connect to.
    # hosts: ["192.168.3.3:9200"]

    #----------------------------- Logstash output --------------------------------
    #output.logstash:
    # The Logstash hosts
#  hosts: ["logstash:5044"]


#----------------------------- kafka output --------------------------------
output.kafka:
  enabled: true
  hosts: [ "192.168.3.3:9092" ]
  topic: sparkzxl-log
```

> 添加kafka输出的配置，将logstash输出配置注释掉。hosts表示kafka的ip和端口号，topic表示filebeat将数据输出到topic为sparksys-log的主题下，此处也根据自己情况修改

#### 2.1.2 Logstash变动

logstash.conf配置input由原来的输入源beat改为kafka

```text
input {
  kafka {
  codec => "json"
  topics => ["sparkzxl-log"]
  bootstrap_servers => "192.168.3.3:9092"
  auto_offset_reset => "latest"
  group_id => "logstash-g1"
  }
}

  output {
  elasticsearch {
  hosts => "es:9200"
  index => "filebeat_%{[fields][log_source]}-%{+YYYY.MM.dd}"
  }
}
```

上述配置说明如下:

- topics后面的sparkzxl-log表示从kafka中topic为sparkzxl-log的主题中获取数据，此处的配置根据自己的具体情况去配置。
- bootstrap_servers表示配置kafka的ip与端口。

到此，ELFK的变动部分结束，接下来就是kafka的搭建

### 2.2 kafka搭建

#### 2.2.1 新建docker-compose.yaml

```yaml
version: '3.2'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    container_name: zookeeper
    volumes:
      - ./zookeeper/data:/data
      - ./zookeeper/datalog:/datalog
    ports:
      - 2181:2181
    restart: always
  kafka:
    image: wurstmeister/kafka
    container_name: kafka
    depends_on:
      - zookeeper
    links:
      - zookeeper
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.3.14:9092
      KAFKA_LISTENERS: PLAINTEXT://:9092
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_LOG_RETENTION_HOURS: 120
      KAFKA_MESSAGE_MAX_BYTES: 10000000
      KAFKA_REPLICA_FETCH_MAX_BYTES: 10000000
      KAFKA_GROUP_MAX_SESSION_TIMEOUT_MS: 60000
      KAFKA_NUM_PARTITIONS: 3
      KAFKA_DELETE_RETENTION_MS: 1000
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./kafka/data:/kafka
    ports:
      - 9092:9092
    restart: always
  kafka-manager:
    image: kafkamanager/kafka-manager
    container_name: kafka-manager
    environment:
      ZK_HOSTS: 192.168.3.3
    ports:
      - 9001:9000
    restart: always
```

#### 2.2.2 创建并启动kafka容器

```bash
docker-compose up -d
```

#### 2.2.3 访问kafka-manager可视化控制台

http://192.168.3.3:9001

- 进入kafka-manager web页面新建cluster

![image.png](https://oss.sparksys.top/halo/image_1594294181023.png)

- 列表展示

![image.png](https://oss.sparksys.top/halo/image_1594294221411.png)

- 进入kafka01

![image.png](https://oss.sparksys.top/halo/image_1594294286350.png)

- 新建topic

![image.png](https://oss.sparksys.top/halo/image_1594294313116.png)

![image.png](https://oss.sparksys.top/halo/image_1594294335454.png)

到此kafka的简单使用完成

### 2.3 ELK + Filebeat + Kafka 分布式日志管理平台使用测试

- Filebeat发送日志到kafka

![image.png](https://oss.sparksys.top/halo/image_1594294809245.png)

- Logstash消费kafka消息，输入日志到es中

![image.png](https://oss.sparksys.top/halo/image_1594294879680.png)

- kabana查看日志

![image.png](https://oss.sparksys.top/halo/image_1594294918462.png)

## 3 总结

1. 在部署的过程中可能会遇到各种情况，此时根据日志说明都可以百度处理（如部署的过程中不能分配内存的问题）。

2. 如果完成后如果数据显示不了，可以先到根据工作流程到各个节点查询数据是否存储和传输成功。如查询filebeat是否成功把数据传输到了kafka，可以进入kafka容器当中使用kafka中如下命令查询：

```bash
bin/kafka-console-consumer.sh –zookeeper localhost:2181 –topic sparkzxl-log –from-beginning
```

查看日志filebeat中的数据是否正常在kafka中存储。

```bash
docker logs -f --tail=200 filebeat
```

3. 该平台的搭建是比较简便的方式，大家可以更加灵活以及动态的配置该平台。

4. 源码下载[elfk部署源码]( https://github.com/sparkzxl/sparkzxl-docker/tree/master/elfk) 相对应kafka的部署源码在上层目录中

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
