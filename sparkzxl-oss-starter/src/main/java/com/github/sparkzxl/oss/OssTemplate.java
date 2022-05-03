package com.github.sparkzxl.oss;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.github.sparkzxl.oss.executor.OssExecutor;
import com.github.sparkzxl.oss.properties.OssProperties;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description:  文件操作模板类
 *
 * @author zhouxinlei
 * @since 2022-05-03 16:17:36
 */
public class OssTemplate implements InitializingBean {

    @Setter
    private OssProperties ossProperties;
    @Setter
    private List<OssExecutor> executors;
    private final Map<Class<? extends OssExecutor>, OssExecutor> executorMap = new LinkedHashMap<>();
    private OssExecutor primaryExecutor;

    public OssTemplate() {

    }


    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    public void createBucket(String bucketName) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.createBucket(bucketName);
    }

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     * @param policy     访问策略
     */
    public void createBucket(String bucketName, Policy policy) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.createBucket(bucketName, policy);
    }

    /**
     * 移除bucket
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.removeBucket(bucketName);
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间 <=7
     * @return url
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expires) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        return ossExecutor.getObjectUrl(bucketName, objectName, expires);
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return url
     */
    public String getObjectUrl(String bucketName, String objectName) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        return ossExecutor.getObjectUrl(bucketName, objectName);
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">API Documentation</a>
     */
    public S3Object getObjectInfo(String bucketName, String objectName) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        return ossExecutor.getObjectInfo(bucketName, objectName);
    }

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
    public PutObjectResult putObject(String bucketName, String objectName, InputStream inputStream, long size,
                                     String contentType) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        return ossExecutor.putObject(bucketName, objectName, inputStream, size, contentType);
    }

    /**
     * 分片上传
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param multipartFile 上传文件
     * @return CompleteMultipartUploadResult
     */
    public UploadResult multipartUpload(String bucketName, String objectName, MultipartFile multipartFile) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        return ossExecutor.multipartUpload(bucketName, objectName, multipartFile);
    }


    /**
     * 分片上传
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param inputStream 文件流
     * @param size        文件大小
     * @return CompleteMultipartUploadResult
     */
    public UploadResult multipartUpload(String bucketName, String objectName, InputStream inputStream, String contentType, long size) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        return ossExecutor.multipartUpload(bucketName, objectName, inputStream, contentType, size);
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     */
    public void removeObject(String bucketName, String objectName) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.removeObject(bucketName, objectName);
    }

    /**
     * 下载文件
     *
     * @param fileUrl  文件路径
     * @param response 响应
     */
    public void downloadFile(String fileUrl, HttpServletResponse response) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.downloadFile(fileUrl, response);
    }


    public void downloadFile(String fileUrl, String fileName, HttpServletResponse response) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.downloadFile(fileUrl, fileName, response);
    }

    protected OssExecutor obtainExecutor(Class<? extends OssExecutor> clazz) {
        if (null == clazz || clazz == OssExecutor.class) {
            return primaryExecutor;
        }
        final OssExecutor ossExecutor = executorMap.get(clazz);
        Assert.notNull(ossExecutor, String.format("can not get bean type of %s", clazz));
        return ossExecutor;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.isTrue(ossProperties.isEnabled(), "tryTimeout must least 0");
        Assert.notNull(ossProperties.getStore(), "file operate store must be not null");
        for (OssExecutor executor : executors) {
            executorMap.put(executor.getClass(), executor);
        }

        final Class<? extends OssExecutor> primaryExecutor = ossProperties.getPrimaryExecutor();
        if (null == primaryExecutor) {
            this.primaryExecutor = executors.get(0);
        } else {
            this.primaryExecutor = executorMap.get(primaryExecutor);
            Assert.notNull(this.primaryExecutor, "primaryExecutor must be not null");
        }
    }
}
