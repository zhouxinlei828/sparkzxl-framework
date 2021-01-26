package com.github.sparkzxl.core.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * description: 校验url是否一致
 *
 * @author: zhouxinlei
 * @date: 2020-07-27 21:14:20
 */
public class StringHandlerUtils {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static boolean isIgnore(List<String> list, String currentUri) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return list.stream().anyMatch((url) ->
                currentUri.startsWith(url) || ANT_PATH_MATCHER.match(url, currentUri)
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
