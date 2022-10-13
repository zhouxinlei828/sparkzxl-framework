package com.github.sparkzxl.oss.executor;

import com.github.sparkzxl.oss.client.OssClient;
import com.github.sparkzxl.spi.SPI;

/**
 * description: Oss Executor Factory
 *
 * @author zhouxinlei
 * @since 2022-10-12 08:46:03
 */
@SPI
public interface OssExecutorFactory {

    /**
     * create OssExecutor
     *
     * @param ossClient   客户端client
     * @return OssExecutor
     */
    OssExecutor create(OssClient ossClient);

}
