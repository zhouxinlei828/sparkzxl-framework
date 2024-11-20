package com.github.sparkzxl.oss.entity;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * description: OssObject
 *
 * @author zhouxinlei
 * @since 2024-11-20 11:38:01
 */
@Data
public class OssObject implements Serializable {

    /**
     * The name of the bucket in which this object is contained
     */
    private String bucketName;

    /**
     * The key under which this object is stored
     */
    private String key;

    private InputStream objectContent;
}
