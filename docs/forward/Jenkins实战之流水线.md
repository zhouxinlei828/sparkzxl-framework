# Jenkins部署之流水线实战
## 1. Jenkins搭建流程
### 1.1 安装环境

|软件环境|简介|版本|RAM
|-------|-------|-------|-------|
|centos|Linux操作系统|7.8|8G|
|docker|docker虚拟化环境|20.10.6||
|docker-compose|docker容器编排|1.29.1||

### 1.2 安装Jenkins
1. 新建Jenkins持久化volume目录
```shell
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
```shell
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
```Shell
docker logs jenkins
```
![image.png](https://oss.sparksys.top/halo/image_1585186880497.png)
- 方式2：从挂载目录/usr/local/docker/jenkins/data中获取
```Shell
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


