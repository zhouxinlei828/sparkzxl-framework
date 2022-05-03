package com.github.sparkzxl.oss.executor;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

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
     * 创建bucket
     *
     * @param bucketName bucket名称
     * @param policy     访问策略
     */
    void createBucket(String bucketName, Policy policy);

    /**
     * 移除bucket
     *
     * @param bucketName bucket名称
     */
    void removeBucket(String bucketName);

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
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param inputStream 文件流
     * @param size        大小
     * @param contentType 类型
     * @return PutObjectResult
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/PutObject">AWS API Documentation</a>
     */
    PutObjectResult putObject(String bucketName, String objectName, InputStream inputStream, long size,
                              String contentType);

    /**
     * 分段上传
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param path       上传路径
     * @return UploadResult
     */
    UploadResult multipartUpload(String bucketName, String objectName, String path);

    /**
     * 分段上传
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param multipartFile 上传文件
     * @return UploadResult
     */
    UploadResult multipartUpload(String bucketName, String objectName, MultipartFile multipartFile);


    /**
     * 分段上传
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param inputStream 文件流
     * @param contentType contentType
     * @param fileLength  文件大小
     * @return UploadResult
     */
    UploadResult multipartUpload(String bucketName, String objectName, InputStream inputStream, String contentType, long fileLength);

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
     * @param fileUrl  文件路径
     * @param response 响应
     */
    void downloadFile(String fileUrl, HttpServletResponse response);

    /**
     * 下载文件
     *
     * @param fileUrl  文件路径
     * @param fileName 文件名
     * @param response 响应
     */
    void downloadFile(String fileUrl, String fileName, HttpServletResponse response);

}
