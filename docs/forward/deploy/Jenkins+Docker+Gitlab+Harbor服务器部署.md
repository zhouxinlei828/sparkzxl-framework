# Jenkins+Docker+Gitlab+Harbor服务器部署

## 1. 部署架构图

![servicearrange.jpg](https://oss.sparksys.top/halo/service-arrange_1585110575536.jpg)

## 2. jenkins安装

### 2.1 新建jenkins安装目录

```bash
mkdir -p /usr/local/docker/jenkins
cd /usr/local/docker/jenkins
touch install.sh
chmod 777 install.sh
```

### 2.2 新建install.sh脚本文件

```bash
#!/bin/bash
echoField "step 1 -> 创建jenkins挂载目录------"
mkdir -p /usr/local/docker/jenkins/jenkins_home
chmod 777 /usr/local/docker/jenkins/jenkins_home
echoField "step 2 -> 创建docker-compose.yaml模板文件------"
cd /usr/local/docker/jenkins/
cat <<EOF > docker-compose.yaml
version: '3'
services:
  jenkins:
   image: 'jenkins/jenkins:lts'
   container_name: jenkins
   restart: always
   environment:
    - 'JAVA_OPTS=-Duser.timezone=Asia/Shanghai'
   volumes:
    - '/etc/localtime:/etc/localtime'
   ports:
    - '8081:8080'
    - '50000:50000'
   volumes:
    - '/usr/local/docker/jenkins/jenkins_home:/var/jenkins_home'
EOF
echoField "step 3 -> docker-compose启动运行jenkins容器"
docker-compose up -d
sleep 1
docker ps -a
```

### 2.3 运行install.sh脚本文件

```bash
bash install.sh
```

### 2.4 如下图所示，则说明jenkins安装成功

![image.png](https://oss.sparksys.top/halo/image_1585184181507.png)
等待一会

### 2.5 jenkins基本配置

> 运行成功后访问该地址登录Jenkins，访问jenkins地址http://ip:8081，第一次登录需要输入管理员密码

![image.png](https://oss.sparksys.top/halo/image_1585184355186.png)

- 方式1：使用管理员密码进行登录，可以使用以下命令从容器启动日志中获取管理密码

```bash
docker logs jenkins
```

![image.png](https://oss.sparksys.top/halo/image_1585186880497.png)

- 方式2：从挂载目录/usr/local/docker/jenkins/jenkins_home中获取

```bash
cat /usr/local/docker/jenkins/jenkins_home/secrets/initialAdminPassword
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

确保以下插件被正确安装：

1. 远程使用ssh的插件：Publish Over SSH

- 通过系统管理->全局工具配置来进行全局工具的配置，比如maven的配置：

![image.png](https://oss.sparksys.top/halo/image_1585185141913.png)

- 新增maven的安装配置:

![image.png](https://oss.sparksys.top/halo/image_1585185218843.png)

- SSH Server管理(如下图)：若需要多台，点新增再添加即可。需要注意的是，如果没有配置免密登录的话，需要点击高级，勾选Use password authentication, or use a different key
  ，并在Passphrase/Password这一栏中输入虚拟机远程登录密码

![image.png](https://oss.sparksys.top/halo/image_1585185954095.png)

## 3. gitlab安装

### 3.1 下载Gitlab的Docker镜像

```bash
docker pull gitlab/gitlab-ce
```

### 3.2 新建gitlab安装目录

```bash
mkdir -p /usr/local/docker/gitlab
cd /usr/local/docker/gitlab
touch install.sh
chmod 777 install.sh
mkdir config
mkdir data
mkdir logs
```

### 3.3 新建install.sh脚本文件

```bash
#!/bin/bash
docker run --detach \
  --publish 10443:443 --publish 8082:80 --publish 1022:22 \
  --name gitlab \
  --restart always \
  --volume /usr/local/docker/gitlab/config:/etc/gitlab \
  --volume /usr/local/docker/gitlab/logs:/var/log/gitlab \
  --volume /usr/local/docker/gitlab/data:/var/opt/gitlab gitlab/gitlab-ce:latest
```

> 需要注意的是我们的Gitlab的http服务运行在宿主机的8082端口上，这里我们将Gitlab的配置，日志以及数据目录映射到了宿主机的指定文件夹下，防止我们在重新创建容器后丢失数据。

### 3.4 运行install.sh脚本文件

```bash
bash install.sh
```

### 3.5 查看gitlab安装是否成功

```bash
docker ps -a
```

如下图所示则表示成功，gitlab安装有点大，需要耐心等待一段时间
![image.png](https://oss.sparksys.top/halo/image_1585187082936.png)

### 3.6 开启防火墙的指定端口

```bash
# 开启1080端口
firewall-cmd --zone=public --add-port=1080/tcp --permanent 
# 重启防火墙才能生效
systemctl restart firewalld
# 查看已经开放的端口
firewall-cmd --list-ports
```

> 在云服务器上，需要到服务器管理界面开放端口号

### 3.7 访问Gitlab

- 访问地址：http://ip:8082/
- 由于Gitlab启动比较慢，需要耐心等待10分钟左右，如果Gitlab没有启动完成访问，会出现如下错误。

![image.png](https://oss.sparksys.top/halo/image_1585187853975.png)

- 可以通过docker命令动态查看容器启动日志来知道gitlab是否已经启动完成

![image.png](https://oss.sparksys.top/halo/image_1585187913079.png)

```bash
docker logs gitlab -f
```

### 3.8 Gitlab的使用

> Gitlab启动完成后第一次访问，会让你重置root帐号的密码

![image.png](https://oss.sparksys.top/halo/image_1585187975656.png)

- 重置完成后输入帐号密码登录

![image.png](https://oss.sparksys.top/halo/image_1585188003151.png)

- 选择创建项目、创建组织、创建帐号

![image.png](https://oss.sparksys.top/halo/image_1585188072617.png)

- 创建组织

> 首先我们需要创建一个组织，然后在这个组织下分别创建用户和项目，这样同组织的用户就可以使用该组织下的项目了

![image.png](https://oss.sparksys.top/halo/image_1585372789941.png)

- 创建用户并修改密码

![image.png](https://oss.sparksys.top/halo/image_1585188286417.png)

![image.png](https://oss.sparksys.top/halo/image_1585188306646.png)

- 修改密码

![image.png](https://oss.sparksys.top/halo/image_1585188377293.png)

- 创建项目

![image.png](https://oss.sparksys.top/halo/image_1585188479442.png)

- 初始化README文件

![image.png](https://oss.sparksys.top/halo/image_1585188580724.png)

![image.png](https://oss.sparksys.top/halo/image_1585188698517.png)

- 将用户分配到组织

![image.png](https://oss.sparksys.top/halo/image_1585188846935.png)

- Git客户端安装及使用

1. 下载Git客户端并安装
2. 下载地址：https://github.com/git-for-windows/git/releases/download/v2.23.0.windows.1/Git-2.23.0-64-bit.exe
3. 下载完成后，一路点击Next安装即可。

- clone项目

![image.png](https://oss.sparksys.top/halo/image_1585189117571.png)

剩下就是常用的git提交代码，拉取代码，在此就不详说，如有不懂可自行百度git命令的使用

## 4. harbor安装

### 4.1 下载harbor安装文件

[https://github.com/goharbor/harbor/releases](https://github.com/goharbor/harbor/releases)

![image.png](https://oss.sparksys.top/halo/image_1585116503795.png)

- 解压如图所示

![image.png](https://oss.sparksys.top/halo/image_1585116581447.png)

### 4.2 修改harbor.yml

修改如图标注地方
![image.png](https://oss.sparksys.top/halo/image_1585189483297.png)

### 4.3 运行install.sh

```bash
./install.sh
```

如图所示，即代表安装成功
![image.png](https://oss.sparksys.top/halo/image_1585189539273.png)

### 4.3 访问harbor页面

> 地址是你设置的域名地址

- 输入用户名和密码，账号是admin，密码是刚才在配置文件配置的harbor_admin_password

![image.png](https://oss.sparksys.top/halo/image_1585189746189.png)

### 4.4 在另外一个服务器(client)登录harbor

![image.png](https://oss.sparksys.top/halo/image_1585189989774.png)

## 5 总结

到此jenkins+docker+gitlab+harbor服务器部署成功，下篇将会讲解如何自动化部署项目

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
