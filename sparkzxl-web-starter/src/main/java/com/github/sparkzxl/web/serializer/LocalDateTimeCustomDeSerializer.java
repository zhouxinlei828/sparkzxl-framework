package com.github.sparkzxl.web.serializer;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.sparkzxl.core.utils.DateUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * description: LocalDateTime自定义序反列化
 *
 * @author: zhouxinlei
 * @date: 2020-11-06 15:54:01
*/
public class LocalDateTimeCustomDeSerializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        long timeStamp = parser.getLongValue();
        DateTime dateTime = DateUtils.date(timeStamp);
        return DateUtils.toLocalDateTime(dateTime);
    }

    @Override
    public Class<?> handledType() {
        return LocalDateTime.class;
    }
}
