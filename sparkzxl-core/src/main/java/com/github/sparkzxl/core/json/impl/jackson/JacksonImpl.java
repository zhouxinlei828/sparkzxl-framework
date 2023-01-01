package com.github.sparkzxl.core.json.impl.jackson;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.github.sparkzxl.core.json.impl.AbstractJSONImpl;
import com.github.sparkzxl.core.json.impl.jackson.JacksonEnhanceModule;
import com.github.sparkzxl.core.support.JwtParseException;
import com.github.sparkzxl.core.util.StrPool;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.util.*;

/**
 * description: JacksonImpl
 *
 * @author zhouxinlei
 * @since 2023-01-01 11:40:42
 */
public class JacksonImpl extends AbstractJSONImpl {

    private static final Logger logger = LoggerFactory.getLogger(JacksonEnhanceModule.class);
    public static final String NAME = "jackson";
    public final ObjectMapper objectMapper = new ObjectMapper();

    public JacksonImpl() {
        objectMapper.setLocale(Locale.CHINA)
                .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature())
                .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature())
                //该特性决定parser将是否允许解析使用Java/C++ 样式的注释（包括'/'+'*' 和'//' 变量）
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                //该特性决定parser是否允许单引号来包住属性名称和字符串值
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.registerModule(new JacksonEnhanceModule());
    }

    @Override
    public String named() {
        return NAME;
    }

    @Override
    public boolean isSupport() {
        try {
            Class<?> aClass = Class.forName("com.fasterxml.jackson.databind.json.JsonMapper");
            return aClass != null;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public String toJson(Object val) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return objectMapper.writeValueAsString(val);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public String toJsonPretty(Object val) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(val);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> T toJavaObject(String json, Type type) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            JavaType javaType = objectMapper.getTypeFactory().constructType(type);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> T toJavaObject(String json, TypeReference<T> typeReference) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            JavaType javaType = objectMapper.getTypeFactory().constructType(typeReference.getType());
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }


    @Override
    public <T> T toJavaObject(Object val, Type type) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            JavaType javaType = objectMapper.getTypeFactory().constructType(type);
            return objectMapper.convertValue(val, javaType);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> List<T> toJavaList(String json, Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            String jsonStr = json;
            if (!StrUtil.startWith(json, StrPool.LEFT_SQ_BRACKET)) {
                jsonStr = StrPool.LEFT_SQ_BRACKET + json + StrPool.RIGHT_SQ_BRACKET;
            }
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(jsonStr, collectionType);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> toMap(String json) {
        try {
            final MapType mapType = objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
            return objectMapper.readValue(json, mapType);
        } catch (Exception e) {
            logger.warn("write to map error: " + json, e);
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> toMap(Object val) {
        try {
            String json = objectMapper.writeValueAsString(val);
            final MapType mapType = objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
            return objectMapper.readValue(json, mapType);
        } catch (Exception e) {
            logger.warn("write to map error: " + val, e);
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> Map<String, T> toMap(String json, Class<T> clazz) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(LinkedHashMap.class, String.class, clazz);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            logger.warn("write to map error: " + json, e);
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T, E> Map<String, T> toMap(E val, Class<T> clazz) {
        try {
            String json = objectMapper.writeValueAsString(val);
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(LinkedHashMap.class, String.class, clazz);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            logger.warn("write to map error: " + val, e);
            throw new JwtParseException(e.getMessage());
        }
    }

}
