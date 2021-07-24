# Jenkins实战之流水线语法详解

> 在前两章节，我们了解了Jenkins流水线的搭建部署以及Jenkins流水线语法的介绍，本章节开始进行实战

- [1.框架使用手册](forward/framework/框架搭建手册之idea搭建代码环境.md)
- [2.Jenkins实战之流水线](forward/deploy/Jenkins实战之流水线.md)
- [3.Jenkins实战之流水线语法详解](forward/deploy/Jenkins实战之流水线语法详解.md)

## 一. 准备环境

|软件环境|简介|版本|RAM
|-------|-------|-------|-------|
|centos|Linux操作系统|7.8|8G|
|docker|docker虚拟化环境|20.10.6|8G|
|docker-compose|docker容器编排|1.29.1|8G|
|harbor镜像仓库|私有镜像仓库|v2.2.1|8G|
|spring boot应用|业务应用代码|2.3.9||

## 二. spring boot应用环境搭建

> 基于谷歌maven jib插件进行镜像打包，详情指令文档可查看[谷歌maven jib插件](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)

### 新建config.json文件

```json
{
  "version": "1.0-SNAPSHOT",
  "registryName": "172.34.200.34:8603/ltc",
  "appName": "spring-boot-server",
  "env": {
    "dev": {
      "credentialsId": "e295a990-f63b-44ac-8211-c8d02bce38a8",
      "host": "172.34.200.34",
      "containerName": "spring-boot-server-dev",
      "hostPort": 8781,
      "serverPort": 8803
    },
    "test": {
      "credentialsId": "e295a990-f63b-44ac-8211-c8d02bce38a8",
      "host": "172.34.200.34",
      "containerName": "spring-boot-server-test",
      "hostPort": 8782,
      "serverPort": 8803
    },
    "prod": {
      "credentialsId": "e295a990-f63b-44ac-8211-c8d02bce38a8",
      "host": "172.34.200.34",
      "containerName": "spring-boot-server-prod",
      "hostPort": 8783,
      "serverPort": 8803
    }
  }
}
```

**注释**

|变量|注释|
|-------|-------|
|version|应用版本号|
|registryName|镜像仓库地址，例如172.34.200.34:8603/demo|
|host|目标主机地址|
|credentialsId|主机登录凭证id|
|containerName|业务应用容器名称|
|hostPort|业务应用容器宿主机端口|
|serverPort|业务应用容器源端口|
|appName|应用名称|

### 新建Jenkinsfile文件

> 读取config.json文件字段获取配置项

```text
import groovy.json.JsonSlurper
node {
    currentBuild.result = "SUCCESS"
    echoField "PWD: ${pwd()}"
    env.PRO_ENV = "prod"
    // 默认设置
    env.VERSION = '1.0-SNAPSHOT'
    env.registryName = ''
    env.host = ''
    env.credentialsId = ''
    env.containerName = ''
    env.hostPort = 8080
    env.serverPort = 8080
    def imageName = ''
    def maven_home
    def appName = ''
    try {
        stage('config') {
            maven_home = tool 'maven-3.6.3'
            echoField "Branch: ${env.BRANCH_NAME}, Environment: ${env.PRO_ENV}，maven_home：${maven_home}"
            maven_home = tool 'maven-3.6.3'
        }
        stage('Prepare') {
            echoField "1.Prepare Stage"
            checkout scm
            pom = readMavenPom file: 'pom.xml'
            // 读取配置信息
            if(fileExists('config.json')) {
                def str = readFile 'config.json'
                def jsonSlurper = new JsonSlurper()
                def obj = jsonSlurper.parseText(str)

                env.registryName = obj.registryName
                appName = obj.appName
                def envConfig = obj.env[env.PRO_ENV]

                echoField "envConfig: ${envConfig}"

                env.VERSION = obj.version

                env.host = envConfig.host
                env.credentialsId = envConfig.credentialsId
                env.containerName = envConfig.containerName
                env.hostPort = envConfig.hostPort
                env.serverPort = envConfig.serverPort
                imageName = "${env.registryName}/${appName}:${env.PRO_ENV}-${env.VERSION}"
                echoField "VERSION: ${env.VERSION} imageName：${imageName}"
                echoField "host: ${env.host} containerName: ${env.containerName} hostPort: ${env.hostPort} serverPort: ${env.serverPort}"
            }
            sh 'ls'
        }

        stage('Test') {
            echoField "2.Test Stage"
        }

        stage('Build') {
            echoField "3.Build Maven Docker Image Stage"
            sh "${maven_home}/bin/mvn clean package -Dmaven.test.skip=true -DsendCredentialsOverHttp=true"
        }

        stage('Deploy') {
            echoField "4.Deploy Docker Image Stage"
            withCredentials([usernamePassword(credentialsId: env.credentialsId, usernameVariable: 'USER', passwordVariable: 'PWD')]) {
                def otherArgs = "-p ${env.hostPort}:${env.serverPort}" // 区分不同环境的启动参数
                def remote = [:]
                remote.name = 'ssh-deploy'
                remote.allowAnyHosts = true
                remote.host = env.host
                remote.user = USER
                remote.password = PWD
                try {
                    sshCommand remote: remote, command: "docker pull ${imageName} && docker rm -f ${env.containerName}"
                } catch (err) {

                }
                sshCommand remote: remote, command: "docker run -d --name ${env.containerName} --restart=always -v /usr/local/logs/${appName}:/home/${appName}/logs -e --spring.profiles.active='${env.PRO_ENV}' ${otherArgs} ${imageName}"
            }
        }
    } catch (err) {
          currentBuild.result = "FAILURE"
          throw err
    }
}

```

**注释**

|变量|注释|
|-------|-------|
|env.PRO_ENV|定义要发布的环境，一般代码分支作为环境区分（dev,test,prod）|
|env.VERSION|应用版本号|
|env.registryName|镜像仓库地址，例如127.0.0.1:8603/demo|
|env.host|目标主机地址|
|env.credentialsId|主机登录凭证id|
|env.containerName|业务应用容器名称|
|env.hostPort|业务应用容器宿主机端口|
|env.serverPort|业务应用容器源端口|
|imageName|发布镜像名称|
|maven_home|maven目录地址|
|appName|应用名称|

如图所示：

![spring-boot-jenkins.png](https://oss.sparksys.top/sparkzxl-component/spring-boot-jenkins.png)

## 三. Jenkins 流水线任务

### 创建流水线任务

**新建Jenkins流水线任务**

![jenkins-Pipeline-Pipeline.png](https://oss.sparksys.top/sparkzxl-component/jenkins-Pipeline.png)

### 配置流水线任务

![Pipeline-config.png](https://oss.sparksys.top/sparkzxl-component/Pipeline-config.png)

点击保存即可建完Jenkins流水线任务

## 运行流水线

- 打开流水线面板

![run-pipeline.png](https://oss.sparksys.top/sparkzxl-component/run-pipeline.png)

- 运行流水线任务

![run-pipeline-1.png](https://oss.sparksys.top/sparkzxl-component/run-pipeline-1.png)

- 查看运行结果

![run-pipeline-result.png](https://oss.sparksys.top/sparkzxl-component/run-pipeline-result.png)

绿色代表流水线执行成功

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
