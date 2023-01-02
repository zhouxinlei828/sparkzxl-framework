package com.github.sparkzxl.core.json.impl.fastjson.serializer;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.github.sparkzxl.core.json.impl.fastjson.FastJsonSerializer;
import com.github.sparkzxl.core.json.impl.fastjson.codec.LocalTimeCodec;
import com.github.sparkzxl.spi.Join;

import java.time.LocalTime;

/**
 * description: LocalDateTimeJsonSerializer
 *
 * @author zhouxinlei
 * @since 2023-01-01 16:27:07
 */
@Join
public class LocalTimeJsonSerializer implements FastJsonSerializer<LocalTime> {

    @Override
    public Class<LocalTime> type() {
        return LocalTime.class;
    }

    @Override
    public ObjectSerializer serializer() {
        return LocalTimeCodec.INSTANCE;
    }

    @Override
    public ObjectDeserializer deserializer() {
        return LocalTimeCodec.INSTANCE;
    }
}
