package com.github.sparkzxl.web.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.github.sparkzxl.core.enums.Enumerator;
import com.github.sparkzxl.core.jackson.CustomJacksonModule;
import com.github.sparkzxl.core.jackson.CustomJavaTimeModule;
import com.github.sparkzxl.core.serializer.CustomDateDeserializer;
import com.github.sparkzxl.core.serializer.EnumeratorSerializer;
import com.github.sparkzxl.core.serializer.LocalDateTimeCustomDeSerializer;
import com.github.sparkzxl.core.serializer.LocalDateTimeCustomSerializer;
import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * description: Jackson全局配置
 *
 * @author zhouxinlei
 */
@Configuration
public class JacksonConfig {

    @Bean("jackson2ObjectMapperBuilderCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Map<Class<?>, JsonSerializer<?>> jsonSerializerMap = ImmutableMap.<Class<?>, JsonSerializer<?>>builder()
                .put(Long.class, ToStringSerializer.instance)
                .put(Long.TYPE, ToStringSerializer.instance)
                .put(BigInteger.class, ToStringSerializer.instance)
                .put(BigDecimal.class, ToStringSerializer.instance)
                .put(Date.class, new DateSerializer(true, new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN)))
                .put(LocalDateTime.class, new LocalDateTimeCustomSerializer())
                .put(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                .put(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)))
                .put(Enumerator.class, new EnumeratorSerializer())
                .build();
        Map<Class<?>, JsonDeserializer<?>> jsonDeserializerMap = ImmutableMap.<Class<?>, JsonDeserializer<?>>builder()
                .put(LocalDateTime.class, LocalDateTimeCustomDeSerializer.INSTANCE)
                .put(Date.class, new CustomDateDeserializer())
                .put(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                .put(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)))
                .build();
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializersByType(jsonSerializerMap)
                .deserializersByType(jsonDeserializerMap)
                .featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    /**
     * 全局配置 序列化和反序列化规则
     * addSerializer：序列化 （Controller 返回 给前端的json）
     * 1. Long -> string
     * 2. BigInteger -> string
     * 3. BigDecimal -> string
     * 4. date -> string
     * 5. LocalDateTime -> "yyyy-MM-dd HH:mm:ss"
     * 6. LocalDate -> "yyyy-MM-dd"
     * 7. LocalTime -> "HH:mm:ss"
     * 8. BaseEnum -> {"code": "xxx", "desc": "xxx"}
     *
     * <p>
     * addDeserializer: 反序列化 （前端调用接口时，传递到后台的json）
     * 1.  {"code": "xxx"} -> Enum
     * 2. "yyyy-MM-dd HH:mm:ss" -> LocalDateTime
     * 3. "yyyy-MM-dd" -> LocalDate
     * 4. "HH:mm:ss" -> LocalTime
     *
     * @param builder 构建
     * @return ObjectMapper
     */
    @Bean
    @Primary
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnMissingBean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper
                .setLocale(Locale.CHINA)
                //去掉默认的时间戳格式
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                // 时区
                .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                //Date参数日期格式
                .setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN, Locale.CHINA))

                //该特性决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）。 如果该属性关闭，则如果遇到这些字符，则会抛出异常。JSON标准说明书要求所有控制符必须使用引号，因此这是一个非标准的特性
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
                // 忽略不能转移的字符
                .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
                .findAndRegisterModules()

                //在使用spring boot + jpa/hibernate，如果实体字段上加有FetchType.LAZY，并使用jackson序列化为json串时，会遇到SerializationFeature.FAIL_ON_EMPTY_BEANS异常
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                //忽略未知字段
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                //单引号处理
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        ;
        //反序列化时，属性不存在的兼容处理
        objectMapper
                .getDeserializationConfig()
                .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        objectMapper.registerModules(new CustomJacksonModule(), new CustomJavaTimeModule());
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

}
