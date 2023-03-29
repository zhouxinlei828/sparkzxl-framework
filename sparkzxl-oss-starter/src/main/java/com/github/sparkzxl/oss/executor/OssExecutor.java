package com.github.sparkzxl.oss.executor;

import cn.hutool.core.net.url.UrlBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.github.sparkzxl.oss.enums.BucketPolicyEnum;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Consumer;
import org.springframework.web.multipart.MultipartFile;

/**
 * description: oss 执行器
 *
 * @author zhouxinlei
 * @since 2022-05-03 16:19:41
 */
public interface OssExecutor {

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    void createBucket(String bucketName);

    /**
     * 移除bucket
     *
     * @param bucketName bucket名称
     */
    void removeBucket(String bucketName);

    /**
     * 设置bucket策略
     *
     * @param bucket bucket名称
     * @param policy 桶策略
     */
    void setBucketPolicy(String bucket, BucketPolicyEnum policy);

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间 <=7
     * @return url
     */
    String getObjectUrl(String bucketName, String objectName, Integer expires);


    /**
     * 获取文件访问路径
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return String
     */
    String getObjectUrl(String bucketName, String objectName);

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">API Documentation</a>
     */
    S3Object getObjectInfo(String bucketName, String objectName);

    /**
     * 判断文件是否存在
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return boolean
     */
    boolean exists(String bucketName, String objectName);

    /**
     * 上传文件
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param multipartFile 文件
     */
    void putObject(String bucketName, String objectName, MultipartFile multipartFile);

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param filePath   文件路径
     */
    void putObject(String bucketName, String objectName, String filePath);

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param url        文件地址
     */
    void putObject(String bucketName, String objectName, URL url);

    /**
     * 分段上传
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param multipartFile 上传文件
     */
    void multipartUpload(String bucketName, String objectName, MultipartFile multipartFile);

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     */
    void removeObject(String bucketName, String objectName);

    /**
     * 下载文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param consumer   消费
     */
    void downloadFile(String bucketName, String objectName, Consumer<InputStream> consumer);

    /**
     * 文件url前半段
     *
     * @param bucket bucket名称
     * @return UrlBuilder
     */
    UrlBuilder getObjectPrefixUrl(String bucket);

    /**
     * 销毁
     */
    void showdown();
}
