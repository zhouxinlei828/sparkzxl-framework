package com.github.sparkzxl.core.json.impl.fastjson.serializer;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.github.sparkzxl.core.json.impl.fastjson.FastJsonSerializer;
import com.github.sparkzxl.core.json.impl.fastjson.codec.LocalDateTimeCodec;
import com.github.sparkzxl.spi.Join;
import java.time.LocalDateTime;

/**
 * description: LocalDateTimeJsonSerializer
 *
 * @author zhouxinlei
 * @since 2023-01-01 16:27:07
 */
@Join
public class LocalDateTimeJsonSerializer implements FastJsonSerializer<LocalDateTime> {

    @Override
    public Class<LocalDateTime> type() {
        return LocalDateTime.class;
    }

    @Override
    public ObjectSerializer serializer() {
        return LocalDateTimeCodec.INSTANCE;
    }

    @Override
    public ObjectDeserializer deserializer() {
        return LocalDateTimeCodec.INSTANCE;
    }
}
