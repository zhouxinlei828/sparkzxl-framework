## 1.为什么使用nexus

> 如果没有私服，我们所需的所有构件都需要通过maven的中央仓库和第三方的Maven仓库下载到本地，而一个团队中的所有人都重复的从maven仓库下载构件无疑加大了仓库的负载和浪费了外网带宽，如果网速慢的话，还会影响项目的进程。很多情况下项目的开发都是在内网进行的，连接不到maven仓库怎么办呢？开发的公共构件怎么让其它项目使用？这个时候我们不得不为自己的团队搭建属于自己的maven私服，这样既节省了网络带宽也会加速项目搭建的进程，当然前提条件就是你的私服中拥有项目所需的所有构件，其次稍微大型公司都有自己的内部架构组件，这些jar包不会被开放，放在nexus也是个不错的选择

[nexus官网地址](https://www.sonatype.com/products/repository-pro)

## 2. nexus安装

### 2.1 安装环境

|软件环境|简介|版本|RAM
|-------|-------|-------|-------|
|centos|Linux操作系统|7.8|8G|
|docker|docker虚拟化环境|20.10.6||
|docker-compose|docker容器编排|1.29.1||

### 2.2 安装nexus应用

1. 新建nexus目录

```bash
mkdir nexus
cd nexus
touch docker-compose.yaml
vim docker-compose.yaml
```

2. 配置docker-compose.yaml文件

```yaml
version: "3.7"
services:
  nexus:
    image: sonatype/nexus3
    container_name: nexus
    environment:
      - "INSTALL4J_ADD_VM_PARAMS=-Xms1g -Xmx1g -XX:MaxDirectMemorySize=1g"
    ports:
      - 8764:8081
    volumes:
      - ./data:/nexus-data
    restart: always
```

**注意事项**

- INSTALL4J_ADD_VM_PARAMS：设置nexus占用的内存，默认是2g

3. 运行docker-compose文件

```bash
docker-compose up -d
```

看见done，说明安装完成

### 2.3 连接nexus

访问http//:127.0.0.1:8764出现nexus可视化页面

![image.png](https://oss.sparksys.top/halo/image_1620093389230.png)
默认账户是：admin 密码：admin

![image.png](https://oss.sparksys.top/halo/image_1620093512183.png)

### 2.4设置maven私库连接

> 我们设置国内的阿里云maven源，通过走nexus进行代理阿里云maven源

![image.png](https://oss.sparksys.top/halo/image_1620093622069.png)

1. 设置阿里云仓库代理

- 新建仓库，选择maven2(proxy),进入新增仓库页面

![image.png](https://oss.sparksys.top/halo/image_1620093732825.png)

- 设置名称，代理源地址，版本类型

> 阿里云maven仓库地址：http://maven.aliyun.com/nexus/content/groups/public/

![image.png](https://oss.sparksys.top/halo/image_1620093938009.png)

- 新建完如图所示

![image.png](https://oss.sparksys.top/halo/image_1620094008855.png)

2. 按照上述步骤，设置maven仓库代理源
3. 阿里云maven仓库和中央仓库源设置完成后，再继续设置默认的maven-public仓库

![image.png](https://oss.sparksys.top/halo/image_1620094302953.png)

![image.png](https://oss.sparksys.top/halo/image_1620094405075.png)

4. 到此基本的nexus的仓库设置结束

## 3. 使用nexus仓库

### 3.1 修改maven目录中的settings.xml文件

如图所示

![image.png](https://oss.sparksys.top/halo/image_1620094562669.png)

1. 找到**localRepository**标签，设置你需要本地存储的maven仓库目录，也可以默认不设置

![image.png](https://oss.sparksys.top/halo/image_1620094658689.png)

2. 找到**servers**节点标签，设置nexus的账户和密码，这边是需要设置，因为maven项目需要读取server节点的账户和密码进行nexus的数据连接

![image.png](https://oss.sparksys.top/halo/image_1620094760482.png)

```xml

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
```

3. 找到**mirrors**节点标签，设置maven镜像源，这边就是设置maven仓库的镜像地址

![image.png](https://oss.sparksys.top/halo/image_1620094908278.png)

```xml

<mirrors>
    <mirror>
        <id>nexus-public</id>
        <mirrorOf>central</mirrorOf>
        <name>central repository</name>
        <url>http://27.0.0.1:8764/repository/maven-public/</url>
    </mirror>
</mirrors>
```

设置的依据看**maven-public**仓库连接地址：
![image.png](https://oss.sparksys.top/halo/image_1620095103377.png)

### 3.2 设置maven项目的pom.xml文件

#### 3.2.1 内部上传组件pom结构

1. 添加**distributionManagement**节点

```xml

<distributionManagement>
    <repository>
        <id>maven-releases</id>
        <url>http://127.0.0.1:8764/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>maven-snapshots</id>
        <url>http://127.0.0.1:8764/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

> id标签对应之前的**server**标签里面的id字段

![image.png](https://oss.sparksys.top/halo/image_1620095921279.png)

2. 添加上传nexus配置

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*</include>
            </includes>
            <filtering>true</filtering>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
    </resources>

    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.0</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <!-- resources资源插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <delimiters>
                        <delimiter>@</delimiter>
                    </delimiters>
                    <useDefaultDelimiters>false</useDefaultDelimiters>
                    <encoding>UTF-8</encoding>
                    <!-- 后缀为pem、pfx的证书文件 -->
                    <nonFilteredFileExtensions>
                        <nonFilteredFileExtension>pem</nonFilteredFileExtension>
                        <nonFilteredFileExtension>pfx</nonFilteredFileExtension>
                        <nonFilteredFileExtension>p12</nonFilteredFileExtension>
                        <nonFilteredFileExtension>key</nonFilteredFileExtension>
                        <nonFilteredFileExtension>jks</nonFilteredFileExtension>
                        <nonFilteredFileExtension>db</nonFilteredFileExtension>
                        <nonFilteredFileExtension>txt</nonFilteredFileExtension>
                    </nonFilteredFileExtensions>
                </configuration>
            </plugin>
            <!--配置生成源码包 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>3.1.0</version>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </pluginManagement>
    <plugins>
        <!-- 编译插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
        </plugin>
        <!-- 资源插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-resources-plugin</artifactId>
        </plugin>
        <!--配置生成源码包 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-source-plugin</artifactId>
        </plugin>
    </plugins>
</build>
<profiles>
    <profile>
        <!-- 打包的 -P参数 -->
        <id>release</id>
        <build>
            <plugins>
                <!-- Source -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-source-plugin</artifactId>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>jar-no-fork</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <!-- Javadoc -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-javadoc-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
        <distributionManagement>
            <repository>
                <id>maven-releases</id>
                <url>http://127.0.0.1:8764/repository/maven-releases/</url>
            </repository>
            <snapshotRepository>
                <id>maven-snapshots</id>
                <url>http://127.0.0.1:8764/repository/maven-snapshots/</url>
            </snapshotRepository>
        </distributionManagement>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
</profiles>
```

> snapshotRepository和repository设置你需要上传的仓库地址：

![image.png](https://oss.sparksys.top/halo/image_1620095852552.png)

3. 到此，上传jar包到maven私库nexus的配置完成，点击deploy试试吧

![image.png](https://oss.sparksys.top/halo/image_1620096139879.png)

#### 3.2.2 使用nexus maven pom设置

新增repositories节点，连接nexus特定的仓库

```xml
<repositories>
    <repository>
        <id>maven-releases</id>
        <url>http://127.0.0.1:8764/repository/maven-releases/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>maven-snapshots</id>
        <url>http://127.0.0.1:8764/repository/maven-snapshots/</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

到此maven私库就可以正常使用了

## 4 总结

maven私库在日常开发中占据着很大的作用，希望我的这边文章能给小白们少一点踩坑，更好地提高工作效率，愉快的摸鱼~

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
