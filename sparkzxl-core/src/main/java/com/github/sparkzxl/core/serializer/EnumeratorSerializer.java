package com.github.sparkzxl.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.sparkzxl.core.enums.Enumerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 枚举序列化类
 *
 * @author zhouxinlei
 */
public class EnumeratorSerializer extends JsonSerializer<Enumerator> {

    @Override
    public void serialize(Enumerator enumerator, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        Map<String, Object> enumeratorMap = new HashMap<>(2);
        enumeratorMap.put("code", enumerator.getCode());
        enumeratorMap.put("desc", enumerator.getDesc());
        jsonGenerator.writeObject(enumeratorMap);
    }

}
