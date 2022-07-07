package com.github.sparkzxl.core.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.JwtParseException;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.core.util.StrPool;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * description:  Jackson封装工具类
 *
 * @author zhouxinlei
 */
@Slf4j
public class JsonUtil {

    public static <T> String toJson(T value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        return Try.of(() -> getInstance().writeValueAsString(value)).getOrElseThrow(e ->
                new BizException(ResultErrorCode.JSON_TRANSFORM_ERROR.getErrorCode(), e.getMessage()));
    }

    public static <T> String toJsonPretty(Object value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        return Try.of(() -> getInstance().writerWithDefaultPrettyPrinter().writeValueAsString(value)).getOrElseThrow(e ->
                new BizException(ResultErrorCode.JSON_TRANSFORM_ERROR.getErrorCode(), e.getMessage()));
    }

    public static byte[] toJsonAsBytes(Object object) {
        if (ObjectUtils.isEmpty(object)) {
            return null;
        }
        return Try.of(() -> getInstance().writeValueAsBytes(object)).getOrElseThrow(e ->
                new BizException(ResultErrorCode.JSON_TRANSFORM_ERROR.getErrorCode(), e.getMessage()));
    }

    public static <T> T parse(String content, Class<T> valueType) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        return Try.of(() -> getInstance().readValue(content, valueType)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> T parse(String content, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        return Try.of(() -> getInstance().readValue(content, typeReference)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> T parse(byte[] bytes, Class<T> valueType) {
        return Try.of(() -> getInstance().readValue(bytes, valueType)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> T parse(byte[] bytes, TypeReference<T> typeReference) {
        return Try.of(() -> getInstance().readValue(bytes, typeReference)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> T parse(InputStream in, Class<T> valueType) {
        return Try.of(() -> getInstance().readValue(in, valueType)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> T parse(InputStream in, TypeReference<T> typeReference) {
        return Try.of(() -> getInstance().readValue(in, typeReference)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> List<T> parseArray(final String content, Class<T> valueTypeRef) {
        return Try.of(() -> {
            String jsonStr = content;
            if (!StrUtil.startWith(content, StrPool.LEFT_SQ_BRACKET)) {
                jsonStr = StrPool.LEFT_SQ_BRACKET + content + StrPool.RIGHT_SQ_BRACKET;
            }
            List<Map<String, Object>> list = getInstance().readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
            return list.stream().map((map) -> toPojo(map, valueTypeRef)).collect(Collectors.toList());
        }).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static Map toMap(String content) {
        return Try.of(() -> getInstance().readValue(content, Map.class)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> Map toMap(T val) {
        ObjectMapper instance = getInstance();
        return Try.of(() -> instance.readValue(instance.writeValueAsString(val), Map.class)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> Map<String, T> toMap(String content, Class<T> valueTypeRef) {
        ObjectMapper instance = getInstance();
        return Try.of(() -> {
            Map<String, Map<String, Object>> map = instance.readValue(content, new TypeReference<Map<String, Map<String, Object>>>() {});
            Map<String, T> result = new HashMap<>(map.size());
            map.forEach((key, value) -> result.put(key, toPojo(value, valueTypeRef)));
            return result;
        }).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T, E> Map<String, T> toMap(E data, Class<T> valueTypeRef) {
        ObjectMapper instance = getInstance();
        return Try.of(() -> {
            Map<String, Map<String, Object>> map = instance.readValue(toJson(data), new TypeReference<Map<String, Map<String, Object>>>() {});
            Map<String, T> result = new HashMap<>(map.size());
            map.forEach((key, value) -> result.put(key, toPojo(value, valueTypeRef)));
            return result;
        }).getOrElseThrow(e -> new JwtParseException(e.getMessage()));
    }

    public static <T> T toPojo(Map fromValue, Class<T> toValueType) {
        return Try.of(() -> getInstance().convertValue(fromValue, toValueType)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> T toPojo(String data, Class<T> toValueType) {
        return Try.of(() -> getInstance().readValue(data, toValueType)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static <T> T toPojo(JsonNode resultNode, Class<T> toValueType) {
        return Try.of(() -> getInstance().convertValue(resultNode, toValueType)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static JsonNode readTree(String data) {
        return Try.of(() -> getInstance().readTree(data)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static JsonNode readTree(InputStream in) {
        return Try.of(() -> getInstance().readTree(in)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static JsonNode readTree(byte[] data) {
        return Try.of(() -> getInstance().readTree(data)).getOrElseThrow(e ->
                new JwtParseException(e.getMessage()));
    }

    public static JsonNode readTree(JsonParser jsonParser) {
        try {
            return getInstance().readTree(jsonParser);
        } catch (IOException e) {
            log.error("json readTree IOException：{}", e.getMessage());
            throw new JwtParseException(e.getMessage());
        }
    }

    public static <T> ObjectNode readTree(T val) {
        try {
            return getInstance().valueToTree(val);
        } catch (IllegalArgumentException e) {
            log.error("json readTree IllegalArgumentException：{}", e.getMessage());
            throw new JwtParseException(e.getMessage());
        }
    }

    public static ObjectMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }

    public static ObjectMapper newInstance() {
        return new JacksonObjectMapper();
    }

    private static class JacksonHolder {
        private final static ObjectMapper INSTANCE = new JacksonObjectMapper();
    }


    public static class JacksonObjectMapper extends ObjectMapper {
        private static final long serialVersionUID = 1L;

        public JacksonObjectMapper() {
            super();
            // 参考BaseConfig
            super.setLocale(Locale.CHINA)
                    .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                    .findAndRegisterModules()
                    .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature())
                    .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature())
                    .enable(JsonParser.Feature.ALLOW_COMMENTS)//该特性决定parser将是否允许解析使用Java/C++ 样式的注释（包括'/'+'*' 和'//' 变量）
                    .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)//该特性决定parser是否允许单引号来包住属性名称和字符串值
                    .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .getDeserializationConfig()
                    .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            super.registerModules(new CustomJacksonModule(), new CustomJavaTimeModule());
            super.findAndRegisterModules();
        }

        @Override
        public ObjectMapper copy() {
            return super.copy();
        }
    }
}
