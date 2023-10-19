package com.github.sparkzxl.oss.executor;

import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.oss.ConfigCache;
import com.github.sparkzxl.oss.client.OssClient;
import com.github.sparkzxl.oss.client.OssClientManager;
import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.oss.provider.OssConfigProvider;
import com.github.sparkzxl.spi.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-10-12 08:42:42
 */
@Slf4j
public class OssExecutorFactoryContext implements ConfigCache, DisposableBean {

    private final Map<String, OssExecutor> executorMap = new ConcurrentHashMap<>();
    private final OssClientManager ossClientManager;
    private final OssConfigProvider configProvider;

    public OssExecutorFactoryContext(OssClientManager ossClientManager,
            OssConfigProvider configProvider) {
        this.ossClientManager = ossClientManager;
        this.configProvider = configProvider;
    }

    public static OssExecutorFactory newInstance(final String ossType) {
        return ExtensionLoader.getExtensionLoader(OssExecutorFactory.class).getJoin(ossType);
    }

    public OssExecutor create(String clientId) {
        Configuration configuration = configProvider.load(clientId);
        return selectOssExecutor(configuration);
    }

    public OssExecutor create() {
        Configuration configuration = configProvider.loadConfigurationList().get(0);
        return selectOssExecutor(configuration);
    }

    private OssExecutor selectOssExecutor(Configuration configuration) {
        ArgumentAssert.notNull(configuration, "Oss Configuration is not available");
        String cacheKey = cacheKey(configuration.getClientType(), configuration.getClientId());
        OssExecutor ossExecutor = executorMap.get(cacheKey);
        OssClient<?> ossClient = ossClientManager.create(configuration);
        if (ossExecutor == null) {
            OssExecutorFactory ossExecutorFactory = newInstance(configuration.getClientType());
            ossExecutor = ossExecutorFactory.create(ossClient);
            executorMap.put(cacheKey, ossExecutor);
            return ossExecutor;
        }
        return ossExecutor;
    }

    @Override
    public String cacheKey(String clientType, String clientId) {
        return clientType.concat("-").concat(clientId);
    }

    @Override
    public void destroy() throws Exception {
        log.info("OssExecutor start closing ....");
        executorMap.forEach((key, value) -> value.showdown());
        executorMap.clear();
        ossClientManager.clear();
        log.info("OssExecutor all closed success,bye");
    }
}
