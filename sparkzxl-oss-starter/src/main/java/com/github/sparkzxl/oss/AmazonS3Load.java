package com.github.sparkzxl.oss;

import com.amazonaws.services.s3.AmazonS3;

/**
 * description: 加载AmazonS3实例
 *
 * @author zhouxinlei
 * @date 2021-10-12 15:05:38
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
