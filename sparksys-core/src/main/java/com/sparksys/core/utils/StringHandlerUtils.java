package com.sparksys.core.utils;

import org.springframework.util.AntPathMatcher;

import java.util.List;
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
        if (list.isEmpty()) {
            return false;
        }
        return list.stream().anyMatch((url) ->
                currentUri.startsWith(url) || ANT_PATH_MATCHER.match(url, currentUri)
        );
    }
}
