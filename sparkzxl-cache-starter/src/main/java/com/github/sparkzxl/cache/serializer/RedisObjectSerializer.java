package com.github.sparkzxl.cache.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.sparkzxl.core.json.impl.jackson.JacksonEnhanceModule;
import com.github.sparkzxl.core.json.impl.jackson.JacksonImpl;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * description: jackson序列化
 *
 * @author zhouxinlei
 */

public class RedisObjectSerializer extends Jackson2JsonRedisSerializer<Object> {

    public RedisObjectSerializer() {
        super(Object.class);
        JacksonImpl jackson = new JacksonImpl();
        ObjectMapper objectMapper = jackson.objectMapper;
        this.setObjectMapper(objectMapper);
    }
}
