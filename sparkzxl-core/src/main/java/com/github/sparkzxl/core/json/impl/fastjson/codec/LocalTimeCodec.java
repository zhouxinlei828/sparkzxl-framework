package com.github.sparkzxl.core.json.impl.fastjson.codec;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * description:  fastjson LocalTimeCodec
 *
 * @author zhouxinlei
 * @since 2023-01-01 15:15:02
 */
public class LocalTimeCodec implements ObjectSerializer, ObjectDeserializer {

    public static final LocalTimeCodec INSTANCE = new LocalTimeCodec(DatePattern.NORM_TIME_PATTERN);
    private final String pattern;

    public LocalTimeCodec(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            LocalTime result = (LocalTime) object;
            out.writeString(result.format(DateTimeFormatter.ofPattern(pattern)));
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String date = parser.parseObject(String.class, fieldName);
        return (T) LocalTime.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
