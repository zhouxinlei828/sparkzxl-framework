package com.github.sparkzxl.oss.client;

import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.spi.ExtensionLoader;

public class OssClientFactory {

    /**
     * New instance IOssClient.
     *
     * @param ossType the oss type
     * @return IOssClient
     */
    public static OssClient<?> newInstance(final String ossType) {
        return ExtensionLoader.getExtensionLoader(OssClient.class).getJoin(ossType);
    }

    public static OssClient<?> buildOssClient(final String ossType, Configuration configuration){
        return newInstance(ossType).init(configuration);
    }

}
