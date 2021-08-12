# 兼容所有云厂商文件存储Spring Boot starter实现

## 1. 背景

在互联网发展的今天，近乎所有的云厂商都提供「对象存储服务」。一种海量、安全、低成本、高可靠的云存储服务，适合存放任意类型的文件。容量和处理能力弹性扩展，多种存储类型供选择，全面优化存储成本。
当我们在使用对应云厂商产品的时候，只需要引入对应尝试提供的 SDK ，根据其开发文档实现即可。但是当我们接入的云厂商较多（或者能够保证接口水平迁移时），我们要根据目标厂商接口「破坏性修改」。 如下提供了几家厂商接口 SDK 上传实例：

### 1.1 阿里云

```java
// Endpoint以杭州为例，其它Region请按实际情况填写。
String endpoint="http://oss-cn-hangzhou.aliyuncs.com";
        String accessKeyId="<yourAccessKeyId>";
        String accessKeySecret="<yourAccessKeySecret>";
// 创建OSSClient实例。
        OSS ossClient=new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
// 创建PutObjectRequest对象。
        String content="Hello OSS";
        PutObjectRequest putObjectRequest=new PutObjectRequest("<yourBucketName>","<yourObjectName>",new ByteArrayInputStream(content.getBytes()));
// 上传字符串。
        ossClient.putObject(putObjectRequest);
// 关闭OSSClient。ossClient.shutdown();
```

### 1.2 华为云

```java
String endPoint="https://your-endpoint";
        String ak="*** Provide your Access Key ***";
        String sk="*** Provide your Secret Key ***";
// 创建ObsClient实例
        ObsClient obsClient=new ObsClient(ak,sk,endPoint);
// localfile为待上传的本地文件路径，需要指定到具体的文件名
        obsClient.putObject("bucketname","objectname",new File("localfile"));
```

### 1.3 七牛云

```java
Configuration cfg=new Configuration(Region.region0());
        UploadManager uploadManager=new UploadManager(cfg);
        String accessKey="your access key";
        String secretKey="your secret key";
        String localFilePath="/home/qiniu/test.png";
        String key=null;Auth auth=Auth.create(accessKey,secretKey);
        String upToken=auth.uploadToken(bucket);
        Response response=uploadManager.put(localFilePath,key,upToken);
```

## 2. 解决方案

### 2.1 Amazon S3 协议

> Amazon 是最早提供对象存储服务 的厂商，制定文件存储相关的业内标准，这意味着只需要实现 S3 协议即可接入兼容此协议的文件存储厂商和中间件。当然 S3 协议不仅仅是技术实现要求标准，对于可用性等都有具体的要求。

### 2.2 兼容 S3 协议国内云厂商

| 名称       | 地址                                               |
| ---------- | -------------------------------------------------- |
| 阿里云 oss | https://help.aliyun.com/document_detail/31947.html |
| 华为云     | https://www.huaweicloud.com                        |
| 腾讯云     | https://cloud.tencent.com                          |
| 七牛云     | https://www.qiniu.com                              |
| 金山云     | https://www.ksyun.com                              |

## 3. 使用 Amazon S3 构建通用 starter

### 3.1 新建 sparkzxl-oss-starter 项目

![sparkzxl-oss-starter.png](https://oss.sparksys.top/sparkzxl-component/sparkzxl-oss-starter-idea.png)

### 3.2 pom 文件配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>sparkzxl-component</artifactId>
        <groupId>com.github.sparkzxl</groupId>
        <version>1.0.1.RELEASE</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>sparkzxl-oss-starter</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.github.sparkzxl</groupId>
            <artifactId>sparkzxl-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk-s3</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 3.3 新建 OssProperties 配置文件

```java
package com.github.sparkzxl.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: oss属性注入
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = "sparkzxl.oss")
public class OssProperties {

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String customDomain;

    /**
     * true path-style nginx 反向代理和S3默认支持 pathStyle {http://endpoint/bucketname} false
     * supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style
     * 模式{http://bucketname.endpoint}
     */
    private Boolean pathStyleAccess = true;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 区域
     */
    private String region;

    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;

    /**
     * Secret key是你账户的密码
     */
    private String secretKey;

    /**
     * 默认的存储桶名称
     */
    private String bucketName = "sparkzxl";

}
```

### 3.3 新建 OssTemplate

> oss 基本操作（增删改查下载）

```java
package com.github.sparkzxl.oss.service;

import cn.hutool.core.util.URLUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.github.sparkzxl.oss.properties.OssProperties;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * aws-s3 通用存储操作 支持所有兼容s3协议的云存储: {阿里云OSS，腾讯云COS，七牛云，京东云，minio 等}
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class OssTemplate implements InitializingBean {

    private final OssProperties ossProperties;

    private AmazonS3 amazonS3;

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    @SneakyThrows
    public void createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket((bucketName));
        }
    }

    /**
     * 获取全部bucket
     * <p>
     *
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListBuckets">AWS
     * API Documentation</a>
     */
    @SneakyThrows
    public List<Bucket> getAllBuckets() {
        return amazonS3.listBuckets();
    }

    /**
     * @param bucketName bucket名称
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListBuckets">AWS
     * API Documentation</a>
     */
    @SneakyThrows
    public Optional<Bucket> getBucket(String bucketName) {
        return amazonS3.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst();
    }

    /**
     * @param bucketName bucket名称
     * @see <a href=
     * "http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteBucket">AWS API
     * Documentation</a>
     */
    @SneakyThrows
    public void removeBucket(String bucketName) {
        amazonS3.deleteBucket(bucketName);
    }

    /**
     * 获取存储桶的访问控制列表
     *
     * @param bucketName bucket名称
     * @return List<Grant>
     */
    public List<Grant> getGrantsAsList(String bucketName) {
        AccessControlList acl = amazonS3.getBucketAcl(bucketName);
        return acl.getGrantsAsList();
    }

    /**
     * 设置存储桶的访问控制权限
     *
     * @param bucketName bucket名称
     * @param email      电子邮件
     * @param permission 权限
     * @see com.amazonaws.services.s3.model.Permission
     */
    public void setGrantsAsList(String bucketName, String email, Permission permission) {
        AccessControlList acl = amazonS3.getBucketAcl(bucketName);
        EmailAddressGrantee grantee = new EmailAddressGrantee(email);
        acl.grantPermission(grantee, permission);
        amazonS3.setBucketAcl(bucketName, acl);
    }

    /**
     * 根据文件前置查询文件
     *
     * @param bucketName bucket名称
     * @param prefix     前缀
     * @param recursive  是否递归查询
     * @return S3ObjectSummary 列表
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListObjects">AWS
     * API Documentation</a>
     */
    @SneakyThrows
    public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
        ObjectListing objectListing = amazonS3.listObjects(bucketName, prefix);
        return new ArrayList<>(objectListing.getObjectSummaries());
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间 <=7
     * @return url
     * @see AmazonS3#generatePresignedUrl(String bucketName, String key, Date expiration)
     */
    @SneakyThrows
    public String getObjectURL(String bucketName, String objectName, Integer expires) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, expires);
        URL url = amazonS3.generatePresignedUrl(bucketName, objectName, calendar.getTime());
        return replaceHttpDomain(url);
    }

    private String replaceHttpDomain(URL url) {
        String objectUrl = url.toString();
        String customDomain = ossProperties.getCustomDomain();
        if (StringUtils.isNotEmpty(customDomain)) {
            String host = URLUtil.getHost(url).toString();
            if (!StringUtils.startsWithAny(customDomain, "http", "https")) {
                customDomain = "https://".concat(customDomain);
            }
            objectUrl = objectUrl.replace(host, customDomain);
        }
        return URLUtil.decode(objectUrl);
    }

    @SneakyThrows
    public String getObjectURL(String bucketName, String objectName) {
        return getObjectUrl(bucketName, objectName);
    }

    public String getObjectUrl(String bucketName, String objectName) {
        URL url = amazonS3.getUrl(bucketName, objectName);
        return replaceHttpDomain(url);
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">AWS
     * API Documentation</a>
     */
    @SneakyThrows
    public S3Object getObject(String bucketName, String objectName) {
        return amazonS3.getObject(bucketName, objectName);
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     * @return PutObjectResult
     * @throws Exception 异常
     */
    public PutObjectResult putObject(String bucketName, String objectName, InputStream stream) throws Exception {
        return putObject(bucketName, objectName, stream, stream.available(), "application/octet-stream");
    }

    /**
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param stream      文件流
     * @param size        大小
     * @param contentType 类型
     * @throws Exception 异常
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/PutObject">AWS
     * API Documentation</a>
     */
    public PutObjectResult putObject(String bucketName, String objectName, InputStream stream, long size,
                                     String contentType) throws Exception {
        byte[] bytes = IOUtils.toByteArray(stream);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contentType);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 上传
        return amazonS3.putObject(bucketName, objectName, byteArrayInputStream, objectMetadata);

    }

    public UploadPartResult uploadPart(UploadPartRequest uploadPartRequest) throws SdkClientException {
        return amazonS3.uploadPart(uploadPartRequest);
    }

    @SneakyThrows
    public CompleteMultipartUploadResult multipartUpload(String bucketName, String objectName, String path) {
        //创建InitiateMultipartUploadRequest对象
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName, objectName);
        // 初始化分片
        InitiateMultipartUploadResult initiateMultipartUploadResult = amazonS3.initiateMultipartUpload(initiateMultipartUploadRequest);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
        String uploadId = initiateMultipartUploadResult.getUploadId();

        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partETags = new ArrayList<PartETag>();
        // 计算文件有多少个分片。
        // 1MB
        final long partSize = 1024 * 1024L;
        final File sampleFile = new File(path);
        long fileLength = sampleFile.length();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }
        // 遍历分片上传。
        for (int i = 0; i < partCount; i++) {
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
            InputStream inputStream = new FileInputStream(sampleFile);
            // 跳过已经上传的分片。
            inputStream.skip(startPos);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(objectName);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(inputStream);
            // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
            uploadPartRequest.setPartSize(curPartSize);
            // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
            uploadPartRequest.setPartNumber(i + 1);
            // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            UploadPartResult uploadPartResult = uploadPart(uploadPartRequest);
            // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
            partETags.add(uploadPartResult.getPartETag());
        }

        // 创建CompleteMultipartUploadRequest对象。
        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);

        // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
        // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);
        return amazonS3.completeMultipartUpload(completeMultipartUploadRequest);
    }

    @SneakyThrows
    public CompleteMultipartUploadResult multipartUpload(String bucketName, String objectName, MultipartFile multipartFile) {
        //创建InitiateMultipartUploadRequest对象
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName, objectName);
        // 初始化分片
        InitiateMultipartUploadResult initiateMultipartUploadResult = amazonS3.initiateMultipartUpload(initiateMultipartUploadRequest);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
        String uploadId = initiateMultipartUploadResult.getUploadId();

        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partTagList = Lists.newArrayList();
        // 计算文件有多少个分片。
        // 1MB
        final long partSize = 1024 * 1024L;
        long fileSize = multipartFile.getSize();
        int partCount = (int) (fileSize / partSize);
        if (fileSize % partSize != 0) {
            partCount++;
        }
        InputStream inputStream = multipartFile.getInputStream();
        // 遍历分片上传。
        return completeMultipartUploadResult(bucketName, objectName, uploadId, partTagList, partSize, fileSize, partCount, inputStream);
    }

    private CompleteMultipartUploadResult completeMultipartUploadResult(String bucketName, String objectName, String uploadId,
                                                                        List<PartETag> partTagList, long partSize, long fileSize, int partCount,
                                                                        InputStream inputStream) throws IOException {
        for (int i = 0; i < partCount; i++) {
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileSize - startPos) : partSize;
            inputStream.skip(startPos);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(objectName);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(inputStream);
            uploadPartRequest.setPartSize(curPartSize);
            uploadPartRequest.setPartNumber(i + 1);
            UploadPartResult uploadPartResult = uploadPart(uploadPartRequest);
            partTagList.add(uploadPartResult.getPartETag());
        }
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partTagList);
        return amazonS3.completeMultipartUpload(completeMultipartUploadRequest);
    }

    @SneakyThrows
    public CompleteMultipartUploadResult multipartUpload(String bucketName, String objectName, InputStream inputStream, long fileLength) {
        //创建InitiateMultipartUploadRequest对象
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName, objectName);
        // 初始化分片
        InitiateMultipartUploadResult initiateMultipartUploadResult = amazonS3.initiateMultipartUpload(initiateMultipartUploadRequest);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
        String uploadId = initiateMultipartUploadResult.getUploadId();

        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partETags = Lists.newArrayList();
        // 计算文件有多少个分片。
        // 1MB
        final long partSize = 1024 * 1024L;
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }
        // 遍历分片上传。
        return completeMultipartUploadResult(bucketName, objectName, uploadId, partETags, partSize, fileLength, partCount, inputStream);
    }

    /**
     * 获取文件信息
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">AWS
     * API Documentation</a>
     */
    public S3Object getObjectInfo(String bucketName, String objectName) {
        return amazonS3.getObject(bucketName, objectName);
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @see <a href=
     * "http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteObject">AWS API
     * Documentation</a>
     */
    public void removeObject(String bucketName, String objectName) {
        amazonS3.deleteObject(bucketName, objectName);
    }

    public void removeObjects(String bucketName, String... objectName) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(objectName);
        amazonS3.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public void afterPropertiesSet() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                ossProperties.getEndpoint(), ossProperties.getRegion());
        AWSCredentials awsCredentials = new BasicAWSCredentials(ossProperties.getAccessKey(),
                ossProperties.getSecretKey());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        this.amazonS3 = AmazonS3Client.builder().withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration).withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding().withPathStyleAccessEnabled(ossProperties.getPathStyleAccess()).build();
    }

}
```

### 3.4 starter 自动装配

- 新建 OssAutoConfiguration 配置文件

```java
package com.github.sparkzxl.oss.config;

import com.github.sparkzxl.oss.http.OssEndpoint;
import com.github.sparkzxl.oss.properties.OssProperties;
import com.github.sparkzxl.oss.service.OssTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: oss自动配置
 *
 * @author zhouxinlei
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    private final OssProperties ossProperties;

    @Bean
    @ConditionalOnMissingBean(OssTemplate.class)
    @ConditionalOnProperty(name = "sparkzxl.oss.enable", havingValue = "true", matchIfMissing = true)
    public OssTemplate ossTemplate() {
        return new OssTemplate(ossProperties);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "sparkzxl.oss.info", havingValue = "true")
    public OssEndpoint ossEndpoint(OssTemplate template) {
        return new OssEndpoint(template);
    }

}
```

- 在 resource 目录新建 META-INF 目录

> 在 META-INF 目录下新建 spring.factories

- spring.factories 添加 config

```text
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    com.github.sparkzxl.oss.config.OssAutoConfiguration
```

## 4. 组件使用

### 4.1 使用

在[maven 中央仓库](https://search.maven.org/) 搜索 sparkzxl-oss-starter

![nexus-sparkzxl-oss-starter.png.png](https://oss.sparksys.top/sparkzxl-component/nexus-sparkzxl-oss-starter.png)

> 支持 MINIO 等自建文件存储

```bash
docker run -p 9000:9000 --name minio1 \
  -e "MINIO_ACCESS_KEY=sparkzxl" \
  -e "MINIO_SECRET_KEY=123456" \
  -v /mnt/data:/data \
  -v /mnt/config:/root/.minio \
  minio/minio server /data
```

```yaml
sparkzxl:
  oss:
    path-style-access: true
    endpoint: http://IP:9000
    access-key: sparkzxl
    secret-key: 123456
    bucketName: sparkzxl
```

在业务代码中使用**OssTemplate**上传即可

### 4.2 源码地址

[sparkzxl-component](https://github.com/sparkzxl/sparkzxl-component.git)

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
