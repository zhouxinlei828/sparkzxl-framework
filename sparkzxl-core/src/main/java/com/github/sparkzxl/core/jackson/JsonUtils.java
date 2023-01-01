package com.github.sparkzxl.core.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.github.sparkzxl.core.support.JwtParseException;
import com.github.sparkzxl.core.util.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.util.*;

/**
 * description:  Jackson封装工具类
 *
 * @author zhouxinlei
 */
@Slf4j
public class JsonUtils {

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

        private static final long serialVersionUID = -6119535000516578610L;

        public JacksonObjectMapper() {
            super();
            // 参考BaseConfig
            super.setLocale(Locale.CHINA)
                    .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                    .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature())
                    .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature())
                    .enable(JsonParser.Feature.ALLOW_COMMENTS)//该特性决定parser将是否允许解析使用Java/C++ 样式的注释（包括'/'+'*' 和'//' 变量）
                    .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)//该特性决定parser是否允许单引号来包住属性名称和字符串值
                    .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false)
                    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            super.registerModule(new JacksonEnhanceModule());
        }

        @Override
        public ObjectMapper copy() {
            return super.copy();
        }
    }

    public static <T> String toJson(T val) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return getInstance().writeValueAsString(val);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    public static <T> String toJsonPretty(Object val) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return getInstance().writerWithDefaultPrettyPrinter().writeValueAsString(val);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    /**
     * 将json字符串转换为实体对象
     *
     * @param json json字符
     * @param type 类型
     * @param <T>  泛型
     * @return T
     */
    public static <T> T toJavaObject(String json, Type type) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return getInstance().readValue(json, getInstance().getTypeFactory().constructType(type));
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    /**
     * 将json字符串转换为实体对象
     *
     * @param json          json字符串
     * @param typeReference 泛型类型
     * @param <T>           泛型
     * @return T
     */
    public static <T> T toJavaObject(String json, TypeReference<T> typeReference) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return getInstance().readValue(json, typeReference);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    /**
     * 将Map对象转换为实体对象
     *
     * @param val  对象val
     * @param type 类型
     * @param <T>  泛型
     * @return T
     */
    public static <T> T toJavaObject(Object val, Type type) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return getInstance().convertValue(val, getInstance().getTypeFactory().constructType(type));
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    /**
     * 将json字符串转换为list实体对象
     *
     * @param json  json字符串
     * @param clazz 类型
     * @param <T>   泛型
     * @return T
     */
    public static <T> List<T> toJavaList(final String json, Class<T> clazz) {
        try {
            String jsonStr = json;
            if (!StrUtil.startWith(json, StrPool.LEFT_SQ_BRACKET)) {
                jsonStr = StrPool.LEFT_SQ_BRACKET + json + StrPool.RIGHT_SQ_BRACKET;
            }
            return getInstance().readValue(jsonStr, getInstance().getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    /**
     * 将json字符串转换为Map对象
     *
     * @param json json字符串
     * @return Map<String, Object>
     */
    public static Map<String, Object> toMap(String json) {
        try {
            final MapType mapType = getInstance().getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
            return getInstance().readValue(json, mapType);
        } catch (Exception e) {
            log.warn("write to map error: " + json, e);
            throw new JwtParseException(e.getMessage());
        }
    }

    /**
     * 将实体对象转换为Map对象
     *
     * @param val 实体对象
     * @return Map<String, Object>
     */
    public static Map<String, Object> toMap(Object val) {
        try {
            String json = getInstance().writeValueAsString(val);
            final MapType mapType = getInstance().getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
            return getInstance().readValue(json, mapType);
        } catch (Exception e) {
            log.warn("write to map error: " + val, e);
            throw new JwtParseException(e.getMessage());
        }
    }

    /**
     * 将json字符串转换为Map对象
     *
     * @param json  json字符串
     * @param clazz 类型
     * @return Map<String, T>
     */
    public static <T> Map<String, T> toMap(final String json, final Class<T> clazz) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            JavaType javaType = getInstance().getTypeFactory().constructParametricType(HashMap.class, String.class, clazz);
            return getInstance().readValue(json, javaType);
        } catch (Exception e) {
            log.warn("write to map error: " + json, e);
            throw new JwtParseException(e.getMessage());
        }
    }

    /**
     * 将实体对象转换为Map对象
     *
     * @param val   实体对象
     * @param clazz 类型
     * @return Map<String, T>
     */
    public static <T, E> Map<String, T> toMap(E val, Class<T> clazz) {
        try {
            String json = getInstance().writeValueAsString(val);
            JavaType javaType = getInstance().getTypeFactory().constructParametricType(HashMap.class, String.class, clazz);
            return getInstance().readValue(json, javaType);
        } catch (Exception e) {
            log.warn("write to map error: " + val, e);
            throw new JwtParseException(e.getMessage());
        }
    }
}
