# 链路追踪实战之JDK镜像制作

## 1. SkyWalking搭建流程

在阅读本文章时，确保已经了解了sky walking基本搭建流程
- [链路追踪实战之SkyWalking极简入门](https://www.iocoder.cn/SkyWalking/install)
- [链路追踪实战之SkyWalking应用搭建](forward/链路追踪实战之SkyWalking环境搭建.md)

### 1.1 安装环境

|软件环境|简介|版本|RAM
|-------|-------|-------|-------|
|centos|Linux操作系统|7.8|8G|
|docker|docker虚拟化环境|20.10.6|8G|
|docker-compose|docker容器编排|1.29.1|8G|

### 1.2 下载jdk

官网下载路径:[javase-jdk8-downloads](https://www.oracle.com/cn/java/technologies/javase/javase-jdk8-downloads.html)

![jdk-download.png](../images/jdk-download.png)

下载对应的系统的jdk版本，本文使用的是Linux操作系统

### 1.3 下载SkyWalking
官网下载路径:[skywalking 8.5.0](https://archive.apache.org/dist/skywalking/8.5.0/)

![skywalking-download.png](../images/skywalking-download.png)

下载对应的版本包，如果使用的是es7作为skywalking 存储db，则需要es7对应的包文件

### 1.4 制作JDK镜像

- 新建Dockerfile
  
```dockerfile
FROM centos:7

MAINTAINER zhouxinlei <zhouxinlei298@163.com>

RUN  cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

ENV LANG="zh_CN.UTF-8"
RUN echo "export LC_ALL=zh_CN.UTF-8"  >>  /etc/profile &&  echo "export LC_ALL=zh_CN.UTF-8" >>/root/.bashrc \
        && localedef -c -f UTF-8 -i zh_CN zh_CN.utf8

RUN cd /usr/share/fonts/ \
        && chmod -R 755 /usr/share/fonts \
        && yum install mkfontscale -y \
        && mkfontscale \
        && yum install fontconfig -y \
        && mkfontdir \
        && fc-cache -fv \
        && mkdir /usr/local/java/ \
        #清理缓存,减少镜像大小
        && yum clean all

ADD jdk-8u281-linux-x64.tar.gz /usr/local/java/
ADD skywalking/ skywalking/

ENV JAVA_HOME /usr/local/java/jdk1.8.0_281
ENV JRE_HOME ${JAVA_HOME}/jre
ENV CLASSPATH .:${JAVA_HOME}/lib:${JRE_HOME}/lib
ENV PATH ${JAVA_HOME}/bin:$PATH

HEALTHCHECK --interval=5s --timeout=2s --retries=10 \
  CMD curl --silent --fail ${HEALTHCHECK_URL} || exit 1

CMD ["/bin/bash"]
```
> - 这边使用的是jdk-8u281版本的jdk
> - HEALTHCHECK_URL：是配置健康检查地址


- JDK镜像构建

文件结构如下
  
![jdk-build.png](jdk-build.png)

```shell
docker build -t java:8 .
```
- 推送镜像

> 打标签，登录阿里云Docker Registry，推送镜像
```shell
docker tag 2467d72208b3 registry.cn-hangzhou.aliyuncs.com/sparkzxl/java:8
docker login --username=admin registry.cn-hangzhou.aliyuncs.com
docker push registry.cn-hangzhou.aliyuncs.com/sparkzxl/java:8
```
> - 2467d72208b3: 镜像id
> - sparkzxl/java:8: 打包镜像名称
  
- 使用镜像
- 运行容器
