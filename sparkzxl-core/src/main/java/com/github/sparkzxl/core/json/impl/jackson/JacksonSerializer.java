package com.github.sparkzxl.core.json.impl.jackson;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.github.sparkzxl.spi.SPI;

/**
 * The interface Jackson serializer.
 *
 * @param <T> the type parameter
 * @author zhouxinlei
 */
@SPI
public interface JacksonSerializer<T> {

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
    JsonSerializer<T> serializer();

    /**
     * Jackson custom deserializer
     *
     * @return json deserializer
     */
    JsonDeserializer<? extends T> deserializer();

}
