package com.github.sparkzxl.oss.executor;

import com.github.sparkzxl.oss.client.OssClient;
import com.github.sparkzxl.spi.Join;

/**
 * description: 阿里云oss执行器工厂类
 *
 * @author zhouxinlei
 * @since 2022-10-12 08:47:30
 */
@Join
public class AliyunOssExecutorFactory implements OssExecutorFactory {


    @Override
    public OssExecutor create(OssClient ossClient) {
        return new AliYunExecutor(ossClient);
    }

}