package com.github.sparkzxl.oss.client;

import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.spi.Join;
import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;

/**
 * description: MinioOssClient
 *
 * @author zhouxinlei
 * @since 2022-10-12 09:14:42
 */
@Setter
@Join
public class MinioOssClient implements OssClient<MinioClient> {

    @Getter
    private MinioClient client;
    private Configuration configuration;

    public MinioOssClient() {
    }

    @Override
    public OssClient<MinioClient> init(Configuration configuration) {
        this.configuration = configuration;
        this.client = MinioClient.builder().endpoint(configuration.getEndpoint())
                .credentials(configuration.getAccessKey(), configuration.getSecretKey())
                .build();
        return this;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}
