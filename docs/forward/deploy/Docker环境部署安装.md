# Docker环境部署安装

## 1 Docker简介

Docker 是一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的镜像中，然后发布到任何流行的 Linux或Windows 机器上，也可以实现虚拟化。容器是完全使用沙箱机制，相互之间不会有任何接口。

Docker从1.13版本之后采用时间线的方式作为版本号，分为社区版CE和企业版EE。 社区版是免费提供给个人开发者和小型团体使用的，企业版会提供额外的收费服务，比如经过官方测试认证过的基础设施、容器、插件等。
社区版按照stable和edge两种方式发布，每个季度更新stable版本，如17.06，17.09；每个月份更新edge版本，如17.09，17.10。

## 2 安装docker

### 2.1  Docker要求

> CentOS 系统的内核版本高于 3.10 ，查看本页面的前提条件来验证你的CentOS 版本是否支持 Docker 。

- 通过 uname -r 命令查看你当前的内核版本

```bash
uname -r
```

- 使用 root 权限登录 Centos。确保 yum 包更新到最新。

```bash
sudo yum update
```

### 2.2 卸载旧版本

```bash
sudo yum remove docker  docker-common docker-selinux docker-engine
```

### 2.3 安装需要的软件包

> yum-util 提供yum-config-manager功能，另外两个是devicemapper驱动依赖

```bash
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
```

### 2.4 设置yum源

```bash
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

![91433322312.png](https://oss.sparksys.top/halo/9-1433322312_1581493743414.png)

### 2.5 选择版本安装

>

```bash
yum list docker-ce --showduplicates | sort -r
```

![00772177322.png](https://oss.sparksys.top/halo/00-772177322_1581493775982.png)

### 2.6 安装docker

```bash
sudo yum install docker-ce -y
```

![87493824081.png](https://oss.sparksys.top/halo/87-493824081_1581493852292.png)

### 2.7 启动并加入开机启动

```bash
sudo systemctl start docker
sudo systemctl enable docker
```

### 2.8 验证安装是否成功

> 有client和service两部分表示docker安装启动都成功了

```bash
docker version
```

![01053107877.png](https://oss.sparksys.top/halo/0-1053107877_1581493989390.png)

### 2.9 镜像加速

您可以通过修改daemon配置文件/etc/docker/daemon.json来使用加速器

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://6c1lhmk3.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

## 3 Docker Compose安装

### 3.1 简介

> Docker Compose是一个用于定义和运行多个docker容器应用的工具。使用Compose你可以用YAML文件来配置你的应用服务，然后使用一个命令，你就可以部署你配置的所有服务了

## 3.2 Docker Compose安装

- 下载docker compose

```bash
curl -L https://get.daocloud.io/docker/compose/releases/download/1.29.1/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
```

- 修改该文件的权限为可执行

```bash
chmod +x /usr/local/bin/docker-compose
```

- 查看是否已经安装成功

```bash
docker-compose --version
```

## 4 使用Docker Compose的步骤

> 使用Dockerfile定义应用程序环境，一般需要修改初始镜像行为时才需要使用； 使用docker-compose.yml定义需要部署的应用程序服务，以便执行脚本一次性部署； 使用docker-compose up命令将所有应用服务一次性部署起来。 docker-compose.yml常用命令

### 4.1 image

指定运行的镜像名称 运行的是mysql5.7的镜像

```yaml
image: mysql:5.7
```

### 4.2 container_name

配置容器名称 容器名称为mysql

```yaml
container_name: mysql
```

### 4.3 ports

指定宿主机和容器的端口映射（HOST:CONTAINER） 将宿主机的3306端口映射到容器的3306端口

```yaml
ports:
  -3306:3306
```

### 4.4 volumes

将宿主机的文件或目录挂载到容器中（HOST:CONTAINER） 将外部文件挂载到myql容器中

```yaml
volumes:
  -/mydata/mysql/log:/var/log/mysql
  -/mydata/mysql/data:/var/lib/mysql
  -/mydata/mysql/conf:/etc/mysql
```

### 4.5 environment

配置环境变量 设置mysql root帐号密码的环境变量

```yaml
environment:
  - MYSQL_ROOT_PASSWORD=root
```

### 4.6 links

连接其他容器的服务（SERVICE:ALIAS） 可以以database为域名访问服务名称为db的容器

```bash
links:
  - db:database
```

## 5 Docker Compose常用命令

### 5.1 构建、创建、启动相关容器：

-d 表示在后台运行

```bash
docker-compose up -d
```

### 5.2 停止所有相关容器：

```bash
docker-compose stop
```

### 5.3 列出所有容器信息：

```bash
docker-compose ps
```

## 6 使用Docker Compose部署应用

### 6.1 编写docker-compose.yml文件

> Docker Compose将所管理的容器分为三层，工程、服务及容器。docker-compose.yml中定义所有服务组成了一个工程，services节点下即为服务，服务之下为容器。容器与容器直之间可以以服务名称为域名进行访问，比如在mall-tiny-docker-compose服务中可以通过jdbc:mysql://db:3306这个地址来访问db这个mysql服务。

```yaml
version:'3'
services:
  db:
    image: mysql:5.7
    container_name: mysql
    ports:
      -3306:3306
    volumes:
      -/mydata/mysql/log:/var/log/mysql
      -/mydata/mysql/data:/var/lib/mysql
      -/mydata/mysql/conf:/etc/mysql
    environment:
      - MYSQL_ROOT_PASSWORD=root
```

### 6.2 运行Docker Compose命令启动所有服务

> 先将docker-compose.yml上传至Linux服务器，再在当前目录下运行如下命令

```bash
docker-compose up docker-compose.yml -d
```

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
