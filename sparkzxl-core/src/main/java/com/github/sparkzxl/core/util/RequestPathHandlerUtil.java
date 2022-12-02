package com.github.sparkzxl.core.util;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.URLUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * description: 校验url是否一致
 *
 * @author zhouxinlei
 */
public class RequestPathHandlerUtil {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static boolean matchUrl(List<String> list, String currentUrl) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        String path = currentUrl;
        if (StringUtils.startsWithAny(currentUrl, StrPool.HTTP, StrPool.HTTPS)) {
            URL url = URLUtil.url(currentUrl);
            path = url.getPath();
        }
        String finalPath = path;
        return list.stream().anyMatch((url) ->
                finalPath.startsWith(url) || ANT_PATH_MATCHER.match(url, finalPath)
        );
    }

    public static <Input> boolean isBlank(Input verifiedData) {
        if (verifiedData != null && verifiedData.getClass().isArray()) {
            return ((Input[]) verifiedData).length == 0;
        } else if (verifiedData instanceof Collection) {
            return ((Collection) verifiedData).isEmpty();
        } else if (verifiedData instanceof Map) {
            return ((Map) verifiedData).isEmpty();
        } else if (verifiedData instanceof CharSequence) {
            return ((CharSequence) verifiedData).length() == 0;
        } else {
            return verifiedData == null;
        }
    }

    public static String underscoreToCamelCase(String str) {
        List<String> split = StrSplitter.split(str, "-", 0, true, true);
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(split)) {
            int index = 0;
            for (String s : split) {
                if (index == 0) {
                    stringBuilder.append(s);
                } else {
                    // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
                    char[] cs = s.toCharArray();
                    cs[0] -= 32;
                    stringBuilder.append(String.valueOf(cs));
                }
                index++;
            }
        } else {
            // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
            char[] cs = str.toCharArray();
            cs[0] -= 32;
            stringBuilder.append(String.valueOf(cs));
        }
        return stringBuilder.toString();
    }
}
