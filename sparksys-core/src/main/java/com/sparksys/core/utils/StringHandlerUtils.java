package com.sparksys.core.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * description: 校验url是否一致
 *
 * @author: zhouxinlei
 * @date: 2020-07-27 21:14:20
 */
@Slf4j
public class StringHandlerUtils {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static boolean isIgnore(List<String> list, String currentUri) {
        log.info("忽略地址：{}", JSON.toJSONString(list));
        log.info("请求地址：{}", currentUri);
        if (list.isEmpty()) {
            return false;
        }
        return list.stream().anyMatch((url) ->
                currentUri.startsWith(url) || ANT_PATH_MATCHER.match(url, currentUri)
        );
    }

}
