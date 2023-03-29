package com.github.sparkzxl.oss.provider;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.URLUtil;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.oss.properties.Configuration;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 文件 加载oss配置信息
 *
 * @author zhouxinlei
 * @since 2022-05-05 13:56:54
 */
public class FileOssConfigProvider extends AbstractOssConfigProvider {

    private final static String HTTP = "http";
    private final static String HTTPS = "https";

    private final List<Configuration> configList;

    public FileOssConfigProvider(String path) {
        String fileStr;
        if (StringUtils.startsWithIgnoreCase(path, HTTP)
                || StringUtils.startsWithIgnoreCase(path, HTTPS)) {
            URL url = URLUtil.url(path);
            File file = FileUtil.file(url);
            FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
            fileStr = fileReader.readString();
        } else {
            fileStr = ResourceUtil.readUtf8Str(path);
        }
        List<Configuration> configList = JsonUtils.getJson().toJavaList(fileStr, Configuration.class);
        for (Configuration configInfo : configList) {
            if (StringUtils.isEmpty(configInfo.getClientType())) {
                throw new RuntimeException("Oss client clientType cannot be empty.");
            }
        }
        this.configList = configList;
    }


    @Override
    public Configuration load(String clientId) {
        Optional<Configuration> optional = configList.stream().filter(config -> config.getClientId().equals(clientId)).findFirst();
        return optional.orElse(null);
    }

    @Override
    protected List<Configuration> list() {
        return configList;
    }
}
