package com.github.sparkzxl.web.config;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.sparkzxl.core.enums.EnumeratorSerializer;
import com.google.common.collect.ImmutableMap;
import com.github.sparkzxl.core.enums.Enumerator;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * description: Jackson全局配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:43:33
 */
@Configuration
public class JacksonConfig {

    @Bean("jackson2ObjectMapperBuilderCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Map<Class<?>, JsonSerializer<?>> jsonSerializerMap = ImmutableMap.<Class<?>, JsonSerializer<?>>builder()
                .put(Long.class, ToStringSerializer.instance)
                .put(Long.TYPE, ToStringSerializer.instance)
                .put(Date.class, new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")))
                .put(Enumerator.class, new EnumeratorSerializer())
                .build();
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializersByType(jsonSerializerMap)
                .featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }
}
