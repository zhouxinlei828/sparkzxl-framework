# sparkzxl-core
> 职能：
> 项目开发过程中所涉及的工具类，包含hutools（糊涂一点也不糊涂的工具类），集合，guava包，itext7 pdf生成工具类，
> mapstruct 实体转换工具类，excel工具类

## POM依赖
```xml
<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-collections4</artifactId>
    </dependency>
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
    </dependency>
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcprov-jdk16</artifactId>
    </dependency>
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
    </dependency>
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>
    <!-- pdfHTML -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>html2pdf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
    </dependency>
    <!-- iText 7 -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itext7-core</artifactId>
        <type>pom</type>
    </dependency>
    <dependency>
        <groupId>javax.mail</groupId>
        <artifactId>mail</artifactId>
        <version>1.4.7</version>
    </dependency>
    <dependency>
        <groupId>com.thoughtworks.xstream</groupId>
        <artifactId>xstream</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-jdk8</artifactId>
    </dependency>
    <dependency>
        <groupId>org.lionsoul</groupId>
        <artifactId>ip2region</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <!--Spring Security RSA工具类-->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-rsa</artifactId>
    </dependency>
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
    </dependency>
    <dependency>
        <groupId>eu.bitwalker</groupId>
        <artifactId>UserAgentUtils</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
    </dependency>
    <!-- easyexcel -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>easyexcel</artifactId>
    </dependency>
</dependencies>
```
## hutools工具类
> 使用方法：[hutool参考文档](https://hutool.cn/docs/)

## PDF工具类
> 生成pdf文档，智能化填充数据，定制化模板

```text
DocumentPdfTemplate pdf生成制定模板类
PdfUtils pdf底层操作方法类
```
- 使用示例
```java
public class PdfTest {

    public static void main(String[] args) {
        DocumentPdfTemplate documentPdfTemplate = new DocumentPdfTemplate();
        String fileName = "test.pdf";
        documentPdfTemplate.fileName = fileName;
        documentPdfTemplate.initPdfData();
        documentPdfTemplate.currPage = 1;
        documentPdfTemplate.top = 785;
        documentPdfTemplate.currLeft = 55;
        documentPdfTemplate.addMainTitle(0, "test");
        documentPdfTemplate.wrap();
        documentPdfTemplate.addText("testhhhhh");
        documentPdfTemplate.getDocument().close();
    }
}
```
- 生成结果
![pdfTest](https://oss.sparkzxl.top/images/pdfTest.jpg)
## mapstruct 实体转换工具
> 使用方法：[Spring Boot 对象转换 MapStruct 入门](http://www.iocoder.cn/Spring-Boot/MapStruct/?self)

## 更多
> 更多详细内容请下载查看代码文档
>[sparkzxl-core-1.2.0.RELEASE-javadoc.jar](http://47.116.52.58:8085/repository/maven-releases/com/sparkzxl/sparkzxl-core/1.2.0.RELEASE/sparkzxl-core-1.2.0.RELEASE-javadoc.jar)

## 使用方法
1. 引入依赖
```xml
<dependency>
    <groupId>com.sparkzxl</groupId>
    <artifactId>sparkzxl-core</artifactId>
    <version>${sparkzxl.version}</version>
</dependency>
```
