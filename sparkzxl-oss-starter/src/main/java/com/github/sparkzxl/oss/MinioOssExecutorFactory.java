package com.github.sparkzxl.oss;

import com.github.sparkzxl.oss.client.OssClient;
import com.github.sparkzxl.oss.executor.MinioExecutor;
import com.github.sparkzxl.oss.executor.OssExecutor;
import com.github.sparkzxl.spi.Join;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-10-12 08:47:30
 */
@Join
public class MinioOssExecutorFactory implements OssExecutorFactory{


    @Override
    public OssExecutor create(OssClient ossClient) {
        return new MinioExecutor(ossClient);
    }
}
