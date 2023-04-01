package com.github.sparkzxl.core.json.impl.fastjson;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.github.sparkzxl.core.json.impl.AbstractJSONImpl;
import com.github.sparkzxl.core.support.JwtParseException;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.spi.ExtensionLoader;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description: FastJsonImpl
 *
 * @author zhouxinlei
 * @since 2023-01-01 11:54:04
 */
public class FastJsonImpl extends AbstractJSONImpl {

    public static final String NAME = "fastjson";
    private static final Logger logger = LoggerFactory.getLogger(FastJsonImpl.class);
    private final SerializeConfig serializeConfig = new SerializeConfig();
    private final ParserConfig parserConfig = new ParserConfig();

    public FastJsonImpl() {
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(BigDecimal.class, ToStringSerializer.instance);
        List<FastJsonSerializer> fastJsonSerializerList = ExtensionLoader.getExtensionLoader(FastJsonSerializer.class).getJoins();
        if (CollectionUtils.isNotEmpty(fastJsonSerializerList)) {
            for (FastJsonSerializer<?> fastJsonSerializer : fastJsonSerializerList) {
                Class<?> type = fastJsonSerializer.type();
                ObjectSerializer serializer = fastJsonSerializer.serializer();
                ObjectDeserializer deserializer = fastJsonSerializer.deserializer();
                if (type != null) {
                    if (serializer != null) {
                        serializeConfig.put(type, serializer);
                    }
                    if (deserializer != null) {
                        parserConfig.putDeserializer(type, deserializer);
                    }
                    logger.info("jackson enhance module load [{}].", fastJsonSerializer.getClass().getName());
                }
            }
        }
    }

    @Override
    public String named() {
        return NAME;
    }

    @Override
    public boolean isSupport() {
        try {
            Class<?> aClass = Class.forName("com.alibaba.fastjson.JSON");
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
            return com.alibaba.fastjson.JSON.toJSONString(val, serializeConfig);
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
            return com.alibaba.fastjson.JSON.toJSONString(val, serializeConfig, SerializerFeature.PrettyFormat);
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
            return com.alibaba.fastjson.JSON.parseObject(json, type, parserConfig);
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

            return com.alibaba.fastjson.JSON.parseObject(json, typeReference.getType(), parserConfig);
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
            String jsonString = JSON.toJSONString(val, serializeConfig);
            return com.alibaba.fastjson.JSON.parseObject(jsonString, type);
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
            return com.alibaba.fastjson.JSON.parseArray(jsonStr, clazz, parserConfig);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> toMap(String json) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return com.alibaba.fastjson.JSON.parseObject(json,
                    TypeToken.getParameterized(LinkedHashMap.class, String.class, Object.class).getType(),
                    parserConfig);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> toMap(Object val) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            String jsonString = JSON.toJSONString(val);
            return com.alibaba.fastjson.JSON.parseObject(jsonString,
                    TypeToken.getParameterized(LinkedHashMap.class, String.class, Object.class).getType(),
                    parserConfig);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> Map<String, T> toMap(String json, Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return com.alibaba.fastjson.JSON.parseObject(json,
                    TypeToken.getParameterized(LinkedHashMap.class, String.class, clazz).getType(),
                    parserConfig);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T, E> Map<String, T> toMap(E val, Class<T> clazz) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            String jsonString = JSON.toJSONString(val);
            return toMap(jsonString, clazz);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

}
