package com.github.sparkzxl.oss.storage;

import io.minio.MinioClient;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * description: minio 存储
 *
 * @author: zhouxinlei
 * @date: 2020-09-12 22:36:51
 */
public class MinioStorage implements Storage {

    private final MinioClient minioClient;

    public MinioStorage(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream, String contentType) throws Exception {
        boolean isExist = minioClient.bucketExists(bucketName);
        if (!isExist) {
            minioClient.makeBucket(bucketName);
        }
        minioClient.putObject(bucketName, objectName, new BufferedInputStream(inputStream), contentType);
    }

    @Override
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(bucketName, objectName);
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        return minioClient.getObjectUrl(bucketName, objectName);
    }

    @Override
    public void removeObject(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(bucketName, objectName);
    }
}
