package com.github.sparkzxl.oss.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

import java.io.InputStream;

/**
 * description: aliyun oss存储
 *
 * @author: zhouxinlei
 * @date: 2020-09-12 22:37:23
 */
public class AliyunStorage implements Storage {

    private final OSS ossClient;
    private final String endpoint;

    public AliyunStorage(OSS ossClient, String endpoint) {
        this.ossClient = ossClient;
        this.endpoint = endpoint;
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        this.ossClient.putObject(bucketName, objectName, inputStream, objectMetadata);
    }

    @Override
    public InputStream getObject(String bucketName, String objectName) {
        return this.ossClient.getObject(bucketName, objectName).getObjectContent();
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName) {
        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }

    @Override
    public void removeObject(String bucketName, String objectName) throws Exception {
        this.ossClient.deleteObject(bucketName, objectName);
    }
}
