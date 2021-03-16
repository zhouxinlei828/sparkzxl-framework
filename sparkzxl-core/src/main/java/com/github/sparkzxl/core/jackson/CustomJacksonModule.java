package com.github.sparkzxl.core.jackson;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.github.sparkzxl.core.enums.Enumerator;
import com.github.sparkzxl.core.serializer.CustomDateDeserializer;
import com.github.sparkzxl.core.serializer.EnumSerializer;
import com.github.sparkzxl.core.serializer.LocalDateTimeCustomDeSerializer;
import com.github.sparkzxl.core.serializer.LocalDateTimeCustomSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * description: 自定义序列化 & 反序列化 规则
 *
 * @author zhouxinlei
*/
public class CustomJacksonModule extends SimpleModule {

    public CustomJacksonModule() {
        super(PackageVersion.VERSION);
        this.addDeserializer(LocalDateTime.class, LocalDateTimeCustomDeSerializer.INSTANCE);
        this.addDeserializer(Date.class, new CustomDateDeserializer());
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));

        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        this.addSerializer(BigInteger.class, ToStringSerializer.instance);
        this.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        this.addSerializer(Date.class, new DateSerializer(true, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        this.addSerializer(LocalDateTime.class, new LocalDateTimeCustomSerializer());
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
        this.addSerializer(Enumerator.class, EnumSerializer.INSTANCE);
    }

}
