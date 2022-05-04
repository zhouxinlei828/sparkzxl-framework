package com.github.sparkzxl.oss.utils;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.nio.charset.Charset;

/**
 * description: oss工具类
 *
 * @author zhouxinlei
 * @since 2022-05-03 17:29:36
 */
public class OssUtils {

    public static String replaceHttpDomain(URL url, String accessDomain) {
        String objectUrl = url.toString();
        if (StringUtils.isNotEmpty(accessDomain)) {
            String buildUrl = UrlBuilder.create()
                    .setScheme(url.getProtocol())
                    .setHost(url.getHost())
                    .setPort(url.getPort()).build();
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(accessDomain, Charset.defaultCharset());
            objectUrl = objectUrl.replace(buildUrl, urlBuilder.build());
        }
        return URLUtil.decode(objectUrl);
    }

    public static String replaceHttpDomain(String url, String accessDomain) {
        UrlBuilder httpUrl = UrlBuilder.ofHttp(url);
        if (StringUtils.isNotEmpty(accessDomain)) {
            String buildUrl = UrlBuilder.create()
                    .setScheme(httpUrl.getScheme())
                    .setHost(httpUrl.getHost())
                    .setPort(httpUrl.getPort()).build();
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(accessDomain, Charset.defaultCharset());
            url = url.replace(buildUrl, urlBuilder.build());
        }
        return URLUtil.decode(url);
    }
}
