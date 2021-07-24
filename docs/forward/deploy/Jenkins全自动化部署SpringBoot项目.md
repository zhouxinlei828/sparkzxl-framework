# Jenkins全自动化部署SpringBoot项目

## 1. 使用Jenkins来实现微服务架构下的自动化部署！

> 在微服务架构中，随着项目拆分越来越细，导致服务越来越多，服务的打包部署就会成为一个相当麻烦的事情。在我之前工作的公司都是本地打包好，上传到服务器上，再创建脚本运行，这样产生的问题就是服务累积越多，部署越耗大量的人力和时间，那有没有什么办法让我们部署一次之后，只要点击执行就可以自动部署呢？当然有！下面我们使用Jenkins来完成一个微服务架构中的自动化一体化部署工作。

### 1.1 环境准备

参考我上一篇：[Jenkins+Docker+Gitlab+Harbor部署环境搭建](forward/deploy/Jenkins+Docker+Gitlab+Harbor服务器部署.md)
本次使用到环境有：

|软件 |版本|描述|
|-------|-------|-------|
|Docker|19.03.8|最近热火的容器技术，实现服务隔离|
|Docker-compose|latest|容器编排工具|
|Harbor|1.10.1|私有镜像仓库服务Harbor|
|Jenkins|latest|Jenkins是开源CI&CD软件领导者， 提供超过1000个插件来支持构建、部署、自动化， 满足任何项目的需要|
|Gitlab|latest|GitLab 是一个用于仓库管理系统的开源项目，使用Git作为代码管理工具|

### 1.2 部署架构图

![servicearrange.jpg](https://oss.sparksys.top/halo/service-arrange_1585110575536.jpg)

## 2 Jenkins自动化部署spring boot项目

### 2.1 代码上传到Gitlab

![image](https://oss.sparksys.top/halo/image_1585373343213.png)

![image.png](https://oss.sparksys.top/halo/image_1585365078824.png)

**上传至代码仓库之后，开始进行jenkins创建任务**

![image.png](https://oss.sparksys.top/halo/image_1585365267487.png)

### 2.2 构建jenkins任务

- 创建任务

![image.png](https://oss.sparksys.top/halo/image_1585365363596.png)

- 添加gitlab项目

![image](https://oss.sparksys.top/halo/image_1585373493194.png)

- 增加构建步骤，调用顶层maven目标

![image.png](https://oss.sparksys.top/halo/image_1585365575137.png)

- 配置maven环境

![image.png](https://oss.sparksys.top/halo/image_1585365684362.png)

> 注意，如果项目是聚合项目，构建项目中的依赖模块，否则当构建可运行的服务模块时会因为无法找到这些模块而构建失败

![image.png](https://oss.sparksys.top/halo/image_1585365858423.png)

```bash
# 只package admin,core,web三个模块
mvn clean package -pl  core,web,admin -am
```

![image.png](https://oss.sparksys.top/halo/image_1585366008839.png)

- 使用额外脚本文件完成maven打包后的自动推送到harbor镜像仓库并启动新容器发布项目

![image](https://oss.sparksys.top/halo/image_1585373622100.png)

```bash
#!/usr/bin/env bash
# 初始化核心参数
# jenkins任务名
task_name='hongneng-test'
# jar包名称
app_name='hongneng-test'
# 发布版本
version='latest'
# harbor镜像仓库域名地址
harbor_registry='www.example.com'
# 镜像仓库
image_prefix='hongneng'
# maven构建版本
maven_version='0.0.1-SNAPSHOT'
# 初始端口
INIT_EXPOSE=8086
# 对外服务端口
EXPOSE=8086
# jenkins任务构建原路径
jenkins_jar_path='/usr/local/docker/jenkins/jenkins_home/workspace/'${task_name}
# 构建镜像路径
projects_path='/usr/local/projects/'

# 停止删除容器
docker stop ${app_name}
echoField 'stop container '${app_name}' success!'
docker rm ${app_name}
echoField 'delete container '${app_name}' success!'

# 复制jar包到指定目录
# 注意：单体maven不需要加${app_name}，聚合项目需要加入${app_name}
cp ${jenkins_jar_path}${app_name}/target/${app_name}-${maven_version}.jar  ${projects_path}${app_name}/
cp ${jenkins_jar_path}${app_name}/src/docker/Dockerfile ${projects_path}${app_name}/

# 构建推送镜像
docker login --username=zhouxinlei --password=Zxl298828 https://${harbor_registry}

docker build -t ${image_prefix}/${app_name}:${maven_version} -f ${projects_path}${app_name}/Dockerfile ${projects_path}${app_name}/.

docker tag ${image_prefix}/${app_name}:${maven_version} ${harbor_registry}/${image_prefix}/${app_name}:${version}

docker push ${harbor_registry}/${image_prefix}/${app_name}:${version}

docker rmi `docker images|grep none | awk '{print $3}'`
docker rmi ${image_prefix}/${app_name}:${maven_version}
# 运行容器
docker run -p ${EXPOSE}:${INIT_EXPOSE} --name ${app_name} -v /etc/localtime:/etc/localtime -v ${projects_path}${app_name}/logs:/var/logs -d ${harbor_registry}/${image_prefix}/${app_name}:${version}
echoField 'run container '${app_name}' success!'
```

- Dockerfile文件编写

在代码源文件src目录下
![image.png](https://oss.sparksys.top/halo/image_1585367064270.png)
![image.png](https://oss.sparksys.top/halo/image_1585367113790.png)

```bash
# 该镜像需要依赖的基础镜像
FROM openjdk:8-jdk-alpine
VOLUME /tmp
# 声明服务运行在8080端口
EXPOSE 8086
# 将当前目录下的jar包复制到docker容器的/目录下
ADD hongneng-test-0.0.1-SNAPSHOT.jar app.jar
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
# 指定维护者的名字
MAINTAINER zhouxinlei
```

- 点击保存，完成hongneng-test 的执行任务创建。

### 2.3 执行jenkins任务

![image.png](https://oss.sparksys.top/halo/image_1585367238826.png)

- 可以在控制台查看maven构建步骤

![image.png](https://oss.sparksys.top/halo/image_1585367334761.png)

- 如下图所示，表示构建发布spring boot项目成功

![image.png](https://oss.sparksys.top/halo/image_1585369829558.png)

![image](https://oss.sparksys.top/halo/image_1585373961872.png)

![image](https://oss.sparksys.top/halo/image_1585374089818.png)

- 查看容器运行状况

![image.png](https://oss.sparksys.top/halo/image_1585370013491.png)

### 2.4 访问项目接口

![image](https://oss.sparksys.top/halo/image_1585374196486.png)

## 3 总结

我们通过在Jenkins中创建任务，完成了微服务架构中服务的打包部署工作，这样当我们每次修改完代码后，只需点击启动任务，就可以实现一键打包部署，省去了频繁打包部署的麻烦。

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
