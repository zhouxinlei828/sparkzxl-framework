package com.sparksys.commons.web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * description: Jackson工具类
 *
 * @author: zhouxinlei
 * @date: 2020-07-11 13:19:02
 */
@Slf4j
public class JacksonUtils {

    private static ObjectMapper objectMapper;

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JacksonUtils.objectMapper = objectMapper;
    }

    public static String writeJsonAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.warn("JsonProcessingException：[{}]", e.getMessage());
            return "";
        }

    }
}
