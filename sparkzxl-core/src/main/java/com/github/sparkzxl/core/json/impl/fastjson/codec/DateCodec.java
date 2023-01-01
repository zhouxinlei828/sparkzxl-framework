package com.github.sparkzxl.core.json.impl.fastjson.codec;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.github.sparkzxl.core.util.DateUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * description:  fastjson DateCodec
 *
 * @author zhouxinlei
 * @since 2023-01-01 15:15:02
 */
public class DateCodec implements ObjectSerializer, ObjectDeserializer {

    private final String pattern;

    public static final DateCodec INSTANCE = new DateCodec(DatePattern.NORM_DATETIME_PATTERN);

    public DateCodec(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            Date result = (Date) object;
            out.writeString(DateUtils.format(result, pattern));
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String date = parser.parseObject(String.class, fieldName);
        return (T) DateUtils.formatDate(date, pattern);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
