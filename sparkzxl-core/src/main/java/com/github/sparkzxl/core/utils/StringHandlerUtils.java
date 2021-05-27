package com.github.sparkzxl.core.utils;

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
public class StringHandlerUtils {

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
}
