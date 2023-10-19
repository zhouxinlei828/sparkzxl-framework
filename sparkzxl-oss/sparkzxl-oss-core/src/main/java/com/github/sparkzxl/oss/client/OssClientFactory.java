package com.github.sparkzxl.oss.client;

import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.oss.support.OssErrorCode;
import com.github.sparkzxl.oss.support.OssException;
import com.github.sparkzxl.spi.ExtensionLoader;
import org.apache.commons.lang3.ObjectUtils;

import java.text.MessageFormat;

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

    public static OssClient<?> buildOssClient(final String ossType, Configuration configuration) {
        OssClient<?> ossClient = newInstance(ossType);
        if (ObjectUtils.isEmpty(ossClient)) {
            String errorMsg = MessageFormat.format(OssErrorCode.OSS_TYPE_UNSUPPORTED.getErrorMsg(),
                    ossType, ossType);
            throw new OssException(OssErrorCode.OSS_TYPE_UNSUPPORTED.getErrorCode(), errorMsg);
        }
        return ossClient.init(configuration);
    }

}
