package com.github.sparkzxl.oss;

import com.amazonaws.services.s3.AmazonS3;

/**
 * description: 加载AmazonS3实例
 *
 * @author zhouxinlei
 */
@FunctionalInterface
public interface AmazonS3Load {

    /**
     * 加载AmazonS3实例
     *
     * @return AmazonS3
     */
    AmazonS3 amazonS3Instance();
}
