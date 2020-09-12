package com.github.sparkzxl.oss.storage;

import java.io.InputStream;

/**
 * description: Storage服务类
 *
 * @author: zhouxinlei
 * @date: 2020-09-12 22:42:38
 */
public interface Storage {


    /**
     * Put object.
     *
     * @param bucketName  the bucket name
     * @param objectName  the object name
     * @param inputStream the input stream
     * @param contentType the content type
     * @throws Exception the exception
     */
    void putObject(String bucketName, String objectName, InputStream inputStream, String contentType) throws Exception;

    /**
     * Gets object.
     *
     * @param bucketName the bucket name
     * @param objectName the object name
     * @return the object
     * @throws Exception the exception
     */
    InputStream getObject(String bucketName, String objectName) throws Exception;

    /**
     * Gets object url.
     *
     * @param bucketName the bucket name
     * @param objectName the object name
     * @return the object url
     * @throws Exception the exception
     */
    String getObjectUrl(String bucketName, String objectName) throws Exception;

    /**
     * Remove object.
     *
     * @param bucketName the bucket name
     * @param objectName the object name
     * @throws Exception the exception
     */
    void removeObject(String bucketName, String objectName) throws Exception;

}
