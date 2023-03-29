package com.github.sparkzxl.oss.client;

import com.github.sparkzxl.oss.ConfigCache;
import com.github.sparkzxl.oss.properties.Configuration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * description: oss client manager
 *
 * @author zhouxinlei
 * @since 2022-10-12 09:30:09
 */
@Slf4j
public class OssClientManager implements ConfigCache {

    private final Map<String, OssClient<?>> clientMap = new ConcurrentHashMap<>();

    public OssClientManager() {
    }

    public OssClient create(Configuration configuration) {
        String ossClientKey = cacheKey(configuration.getClientType(), configuration.getClientId());
        OssClient ossClient = clientMap.get(ossClientKey);
        if (ossClient == null) {
            ossClient = OssClientFactory.buildOssClient(configuration.getClientType(), configuration);
            clientMap.put(ossClientKey, ossClient);
        }
        return ossClient;
    }

    public void clear() {
        clientMap.clear();
    }

    @Override
    public String cacheKey(String clientType, String clientId) {
        return clientType.concat("-").concat(clientId);
    }
}
