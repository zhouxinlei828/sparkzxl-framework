package com.github.sparkzxl.oss.executor;

import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.core.util.ListUtils;
import com.github.sparkzxl.oss.client.OssClient;
import com.github.sparkzxl.oss.properties.Configuration;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * description: 抽象oss执行器
 *
 * @author zhouxinlei
 * @since 2022-05-07 15:27:13
 */
public abstract class AbstractOssExecutor<T> implements OssExecutor {

    protected final OssClient<T> client;

    public AbstractOssExecutor(OssClient<T> client) {
        this.client = client;
    }

    public void uploadFileLimit(String fileName) {
        String extension = FileNameUtils.getExtension(fileName);
        Configuration configuration = obtainConfigInfo();
        String fileFormat = configuration.getFileFormat();
        if (StringUtils.isEmpty(fileFormat)) {
            return;
        }
        List<String> fileFormatList = ListUtils.stringToList(fileFormat);
        ArgumentAssert.isFalse(!fileFormatList.contains(extension), "上传文件格式限制，不允许上传");
    }

    /**
     * 获取当前线程客户端
     *
     * @return T
     */
    protected T obtainClient() {
        return client.getClient();
    }

    /**
     * 获取当前线程配置信息
     *
     * @return OssConfigInfo
     */
    @Override
    public Configuration obtainConfigInfo() {
        return client.getConfiguration();
    }
}
