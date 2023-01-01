package com.github.sparkzxl.core.json.impl.fastjson;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.github.sparkzxl.spi.SPI;

/**
 * The interface fastjson serializer.
 *
 * @param <T> the type parameter
 * @author zhouxinlei
 */
@SPI
public interface FastJsonSerializer<T> {

    /**
     * jackson serializer class type.
     *
     * @return class
     */
    Class<T> type();

    /**
     * Jackson custom serializer
     *
     * @return json serializer
     */
    ObjectSerializer serializer();

    /**
     * Jackson custom deserializer
     *
     * @return json deserializer
     */
    ObjectDeserializer deserializer();

}
