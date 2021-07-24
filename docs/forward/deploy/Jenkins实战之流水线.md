# Jenkins实战之流水线环境搭建

## 1. Jenkins搭建流程

### 1.1 安装环境

|软件环境|简介|版本|RAM
|-------|-------|-------|-------|
|centos|Linux操作系统|7.8|8G|
|docker|docker虚拟化环境|20.10.6|8G|
|docker-compose|docker容器编排|1.29.1|8G|

### 1.2 安装Jenkins

1. 新建Jenkins持久化volume目录

```bash
mkdir jenkins
cd jenkins
mkdir data repository
touch docker-compose.yaml
vim docker-compose.yaml
```

2. 配置docker-compose.yaml文件

```yaml
version: '3'
services:
  jenkins:
    image: jenkinsci/blueocean
    container_name: jenkins
    user: root
    privileged: true
    restart: always
    environment:
      - 'JAVA_OPTS=-Duser.timezone=Asia/Shanghai'
    volumes:
      - /etc/localtime:/etc/localtime
      - ./data:/var/jenkins_home
      - ./repository:/root/.m2/repository
      - /var/run/docker.sock:/var/run/docker.sock
    ports:
      - 8761:8080
      - 50000:50000
```

**注意事项**

- ./data：设置jenkins持久化目录
- ./repository: 设置maven持久化目录
- /var/run/docker.sock:设置docker运行环境

3. 运行docker-compose文件

```bash
docker-compose up -d
```

看见done，说明安装完成

4. 如下图所示，则说明jenkins安装成功

![image.png](https://oss.sparksys.top/halo/image_1585184181507.png)
等待一会

### 1.3 jenkins基本配置

> 运行成功后访问该地址登录Jenkins，访问jenkins地址http://ip:8081，第一次登录需要输入管理员密码

![image.png](https://oss.sparksys.top/halo/image_1585184355186.png)

- 方式1：使用管理员密码进行登录，可以使用以下命令从容器启动日志中获取管理密码

```bash
docker logs jenkins
```

![image.png](https://oss.sparksys.top/halo/image_1585186880497.png)

- 方式2：从挂载目录/usr/local/docker/jenkins/data中获取

```bash
cat /usr/local/docker/jenkins/data/secrets/initialAdminPassword
```

![image.png](https://oss.sparksys.top/halo/image_1585184618794.png)

- 选择安装插件方式，这里我们直接安装推荐的插件

![image.png](https://oss.sparksys.top/halo/image_1585184660409.png)

- 进入插件安装界面，联网等待插件安装：

![image.png](https://oss.sparksys.top/halo/image_1585184687020.png)

- 安装完成后，创建管理员账号：

![image.png](https://oss.sparksys.top/halo/image_1585184745473.png)

- 进行实例配置，配置Jenkins的URL

![image.png](https://oss.sparksys.top/halo/image_1585184779788.png)

- 点击系统管理->插件管理，进行一些自定义的插件安装：

![image.png](https://oss.sparksys.top/halo/image_1585184904799.png)

### 1.4 jenkins插件安装

> jenkins pipeline必备插件

- SSHpipeline Step Plugins
- Config File Provider Plugin
- pipeline-utility-steps
- SSH Pipeline Steps

安装完成后等待重启

### 1.5 配置maven环境

- 进入系统管理

![jenkins-maven-setting1.png](https://oss.sparksys.top/sparkzxl-component/jenkins-maven-setting1.png)

- 新增配置

![maven-configfiles.png](https://oss.sparksys.top/sparkzxl-component/maven-configfiles.png)

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<!--
 | This is the configuration file for Maven. It can be specified at two levels:
 |
 |  1. User Level. This settings.xml file provides configuration for a single user,
 |                 and is normally provided in ${user.home}/.m2/settings.xml.
 |
 |                 NOTE: This location can be overridden with the CLI option:
 |
 |                 -s /path/to/user/settings.xml
 |
 |  2. Global Level. This settings.xml file provides configuration for all Maven
 |                 users on a machine (assuming they're all using the same Maven
 |                 installation). It's normally provided in
 |                 ${maven.conf}/settings.xml.
 |
 |                 NOTE: This location can be overridden with the CLI option:
 |
 |                 -gs /path/to/global/settings.xml
 |
 | The sections in this sample file are intended to give you a running start at
 | getting the most out of your Maven installation. Where appropriate, the default
 | values (values used when the setting is not specified) are provided.
 |
 |-->
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <!-- localRepository
     | The path to the local repository maven will use to store artifacts.
     |
     | Default: ${user.home}/.m2/repository
    <localRepository>/path/to/local/repo</localRepository>
    -->
    <localRepository>/usr/local/maven/repository</localRepository>
    <pluginGroups>
    </pluginGroups>
    <proxies>
    </proxies>

    <!-- servers
     | This is a list of authentication profiles, keyed by the server-id used within the system.
     | Authentication profiles can be used whenever maven must make a connection to a remote server.
     |-->
    <servers>
        <server>
            <id>maven-releases</id>
            <username>admin</username>
            <password>123456</password>
        </server>
        <server>
            <id>maven-snapshots</id>
            <username>admin</username>
            <password>123456</password>
        </server>
    </servers>
    <mirrors>
        <!-- 设置maven nexus地址-->
        <mirror>
            <id>nexus-public</id>
            <mirrorOf>central</mirrorOf>
            <name>central repository</name>
            <url>http://127.0.0.1:8764/repository/maven-public/</url>
        </mirror>
    </mirrors>
    <profiles>
    </profiles>
</settings>
```

- 配置maven插件

![maven-plugin.png](https://oss.sparksys.top/sparkzxl-component/maven-plugin.png)

![maven-settings.png](https://oss.sparksys.top/sparkzxl-component/maven-settings.png)

![maven-install.png](https://oss.sparksys.top/sparkzxl-component/maven-install.png)

### 1.6 配置密码凭证

> 配置密码凭证包括（git账户密码，服务器登录凭证等等）

- 进入系统管理

![credentials-1.png](https://oss.sparksys.top/sparkzxl-component/credentials-1.png)

- 添加凭证

![credentials-2.png](https://oss.sparksys.top/sparkzxl-component/credentials-2.png)

![credentials-3.png](https://oss.sparksys.top/sparkzxl-component/credentials-3.png)

添加完成后，在Jenkins任务中即可使用

## 2. Jenkins pipeline 使用教程

> Pipeline支持两种语法： Declarative Pipeline（声明式pipeline，在pipeline2.5中引入，结构化方式）和Scripted Pipeline（脚本式pipeline），两者都支持建立连续输送的Pipeline。

具体语法可参考：
[Jenkins实战之流水线语法详解](forward/Jenkins实战之流水线语法详解.md)

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
