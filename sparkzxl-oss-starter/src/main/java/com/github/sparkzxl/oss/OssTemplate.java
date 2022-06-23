package com.github.sparkzxl.oss;

import com.amazonaws.services.s3.model.S3Object;
import com.github.sparkzxl.oss.executor.OssExecutor;
import com.github.sparkzxl.oss.properties.OssProperties;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description:  文件操作模板类
 *
 * @author zhouxinlei
 * @since 2022-05-03 16:17:36
 */
@Slf4j
@NoArgsConstructor
public class OssTemplate implements InitializingBean, DisposableBean {

    @Setter
    private OssProperties ossProperties;
    @Setter
    private List<OssExecutor> executors;
    private final Map<Class<? extends OssExecutor>, OssExecutor> executorMap = new LinkedHashMap<>();
    private OssExecutor primaryExecutor;

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
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param multipartFile 文件
     */
    public void putObject(String bucketName, String objectName, MultipartFile multipartFile) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.putObject(bucketName, objectName, multipartFile);
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param filePath   文件地址
     */
    public void putObject(String bucketName, String objectName, String filePath) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.putObject(bucketName, objectName, filePath);
    }

    /**
     * 分片上传
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param multipartFile 上传文件
     */
    public void multipartUpload(String bucketName, String objectName, MultipartFile multipartFile) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.multipartUpload(bucketName, objectName, multipartFile);
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

    public void downloadFile(String bucketName, String objectName, String fileName, HttpServletResponse response) {
        OssExecutor ossExecutor = obtainExecutor(primaryExecutor.getClass());
        ossExecutor.downloadFile(bucketName, objectName, fileName, response);
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
        Assert.notNull(ossProperties.getStore(), "store mode must be not null");
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

    @Override
    public void destroy() {
        log.info("executor start closing ....");
        shutdownExecutor();
        log.info("executor all closed success,bye");
    }

    private void shutdownExecutor() {
        executorMap.clear();
    }
}
