package com.github.sparkzxl.core.json.impl.fastjson.serializer;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.github.sparkzxl.core.json.impl.fastjson.FastJsonSerializer;
import com.github.sparkzxl.core.json.impl.fastjson.codec.LocalDateCodec;
import com.github.sparkzxl.spi.Join;
import java.time.LocalDate;

/**
 * description: LocalDateJsonSerializer
 *
 * @author zhouxinlei
 * @since 2023-01-01 16:27:07
 */
@Join
public class LocalDateJsonSerializer implements FastJsonSerializer<LocalDate> {

    @Override
    public Class<LocalDate> type() {
        return LocalDate.class;
    }

    @Override
    public ObjectSerializer serializer() {
        return LocalDateCodec.INSTANCE;
    }

    @Override
    public ObjectDeserializer deserializer() {
        return LocalDateCodec.INSTANCE;
    }
}
