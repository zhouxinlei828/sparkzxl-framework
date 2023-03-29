package com.github.sparkzxl.core.json.impl.fastjson.serializer;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.github.sparkzxl.core.json.impl.fastjson.FastJsonSerializer;
import com.github.sparkzxl.core.json.impl.fastjson.codec.DateCodec;
import com.github.sparkzxl.spi.Join;
import java.util.Date;

/**
 * description: DateJsonSerializer
 *
 * @author zhouxinlei
 * @since 2023-01-01 16:27:07
 */
@Join
public class DateJsonSerializer implements FastJsonSerializer<Date> {

    @Override
    public Class<Date> type() {
        return Date.class;
    }

    @Override
    public ObjectSerializer serializer() {
        return DateCodec.INSTANCE;
    }

    @Override
    public ObjectDeserializer deserializer() {
        return DateCodec.INSTANCE;
    }
}
