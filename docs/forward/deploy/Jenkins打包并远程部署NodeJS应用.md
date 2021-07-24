# Jenkins打包并远程部署NodeJS应用

## 1.环境准备

- [Jenkins+Docker+Gitlab+Harbor服务器部署](forward/deploy/Jenkins+Docker+Gitlab+Harbor服务器部署.md)
- nodeJs安装

## 2. NodeJs安装

> 从官网下下载最新的nodejs，https://nodejs.org/en/download/

![image.png](https://oss.sparksys.top/halo/image_1586744414503.png)

- 上传node安装包到服务器

1. 先安装lrzsz，用于文件上传与下载

```bash
yum install lrzsz -y
rz node-v12.16.2-linux-x64.tar.xz
```

2. 进行安装nodejs

```bash
tar -xvf node-v12.16.2-linux-x64.tar.xz
```

3. 移动并改名文件夹（不改名也行）

```bash
mv node-v12.16.2-linux nodejs
cp -r nodejs /usr/local
```

4. 软链接方式让npm和node命令全局生效

```bash
ln -s /usr/local/nodejs/bin/npm /usr/local/bin/
ln -s /usr/local/nodejs/bin/node /usr/local/bin/
```

5. 查看nodejs是否安装成功

```bash
node -v
npm -v
```

![image.png](https://oss.sparksys.top/halo/image_1586744912479.png)

## 3. 安装插件并进行nodejs项目部署

### 3.1 安装nodejs插件

![image.png](https://oss.sparksys.top/halo/image_1586745021387.png)

### 3.2 全局配置nodejs环境

- 进入系统管理，全局工具配置

![image.png](https://oss.sparksys.top/halo/image_1586745114122.png)

- 配置nodejs环境，并保存
  ![image.png](https://oss.sparksys.top/halo/image_1586745165234.png)

### 3.3 构建jenkins任务

![image.png](https://oss.sparksys.top/halo/image_1586745315499.png)

- 配置git源码链接

![image](https://oss.sparksys.top/halo/image_1586745601590.png)

- 增加构建步骤

![image.png](https://oss.sparksys.top/halo/image_1586745655984.png)

![image](https://oss.sparksys.top/halo/image_1586745863786.png)

然后保存

- 立即构建

![image.png](https://oss.sparksys.top/halo/image_1586749349163.png)

此时，我们能清晰的看到jenkins已经构建成功

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
