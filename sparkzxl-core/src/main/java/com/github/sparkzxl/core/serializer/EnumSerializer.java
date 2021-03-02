package com.github.sparkzxl.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.sparkzxl.core.enums.Enumerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 继承了BaseEnum接口的枚举值，将会统一按照以下格式序列化
 * {"code": "XX","desc": "xxxx"}
 *
 * @author: zhouxinlei
 * @date: 2021-03-02 13:35:26
*/
public class EnumSerializer extends StdSerializer<Enumerator> {

    public final static EnumSerializer INSTANCE = new EnumSerializer();
    public final static String ALL_ENUM_KEY_FIELD = "code";
    public final static String ALL_ENUM_DESC_FIELD = "desc";

    public EnumSerializer() {
        super(Enumerator.class);
    }

    @Override
    public void serialize(Enumerator distance, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        Map<String, Object> enumeratorMap = new HashMap<>(2);
        enumeratorMap.put(ALL_ENUM_KEY_FIELD, distance.getCode());
        enumeratorMap.put(ALL_ENUM_DESC_FIELD, distance.getDesc());
        generator.writeObject(enumeratorMap);
    }
}
