package com.github.sparkzxl.core.json.impl.jackson;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.github.sparkzxl.constant.enums.Enumerator;
import com.github.sparkzxl.core.serializer.CustomDateDeserializer;
import com.github.sparkzxl.core.serializer.EnumSerializer;
import com.github.sparkzxl.core.serializer.CustomLocalDateTimeDeSerializer;
import com.github.sparkzxl.spi.ExtensionLoader;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * description: Jackson增强模块
 *
 * @author zhouxinlei
 */

public class JacksonEnhanceModule extends SimpleModule {

    private static final long serialVersionUID = -3836634752008167045L;

    private static final Logger logger = LoggerFactory.getLogger(JacksonEnhanceModule.class);

    public JacksonEnhanceModule() {
        // 序列化基础类型
        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        this.addSerializer(BigInteger.class, ToStringSerializer.instance);
        this.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        this.addSerializer(Enumerator.class, EnumSerializer.INSTANCE);

        // 时间序列化
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        this.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN)));
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));

        // 时间反序列化
        this.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        this.addDeserializer(Date.class, new CustomDateDeserializer(DatePattern.NORM_DATETIME_PATTERN));
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));

        List<JacksonSerializer> jacksonSerializerList = ExtensionLoader.getExtensionLoader(JacksonSerializer.class).getJoins();
        if (CollectionUtils.isNotEmpty(jacksonSerializerList)) {
            for (JacksonSerializer jacksonSerializer : jacksonSerializerList) {
                Class type = jacksonSerializer.type();
                JsonSerializer serializer = jacksonSerializer.serializer();
                JsonDeserializer deserializer = jacksonSerializer.deserializer();
                if (type != null) {
                    if (serializer != null) {
                        this.addSerializer(type, serializer);
                    }
                    if (deserializer != null) {
                        this.addDeserializer(type, deserializer);
                    }
                    logger.info("jackson enhance module load [{}].", jacksonSerializer.getClass().getName());
                }
            }
        }

    }

}
