package com.github.sparkzxl.core.util;

import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 文件摘要处理工具类
 *
 * @author zhouxinlei
 */
public class FileDigestUtil {

    public static String extractChecksum(String filePath, DigestAlgorithm algorithm) {
        // 根据算法名称初始化摘要算法
        Digester digester = DigestUtil.digester(algorithm);
        if (StringUtils.startsWithAny(filePath, StrPool.HTTP, StrPool.HTTPS)) {
            URL url = URLUtil.url(filePath);
            try (InputStream inputStream = url.openStream()) {
                return digester.digestHex(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return digester.digestHex(new File(filePath));
        }
    }

    public static String extractChecksum(File file, DigestAlgorithm algorithm) {
        // 根据算法名称初始化摘要算法
        Digester digester = DigestUtil.digester(algorithm);
        return digester.digestHex(file);
    }

    public static String extractChecksum(InputStream inputStream, DigestAlgorithm algorithm) {
        // 根据算法名称初始化摘要算法
        Digester digester = DigestUtil.digester(algorithm);
        return digester.digestHex(inputStream);
    }

    public static String extractChecksum(URL url, DigestAlgorithm algorithm) {
        // 根据算法名称初始化摘要算法
        Digester digester = DigestUtil.digester(algorithm);
        try (InputStream inputStream = url.openStream()) {
            return digester.digestHex(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
