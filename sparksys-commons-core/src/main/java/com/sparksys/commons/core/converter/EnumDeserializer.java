package com.sparksys.commons.core.converter;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;

/**
 * description: 枚举反序列化
 *
 * @author: zhouxinlei
 * @date: 2020-07-09 09:51:25
 */
@Slf4j
public class EnumDeserializer extends StdDeserializer<Enum<?>> {
    public static final EnumDeserializer INSTANCE = new EnumDeserializer();

    public EnumDeserializer() {
        super(Enum.class);
    }

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        JsonToken token = p.getCurrentToken();

        String value;
        for (value = null; !token.isStructEnd(); token = p.getCurrentToken()) {
            if ("code".equals(p.getText())) {
                p.nextToken();
                value = p.getValueAsString();
            } else {
                p.nextToken();
            }
        }

        if (value != null && !"".equals(value)) {
            Object obj = p.getCurrentValue();
            if (obj == null) {
                return null;
            } else {
                Field field = ReflectUtil.getField(obj.getClass(), p.getCurrentName());
                if (field == null) {
                    return null;
                } else {
                    Class fieldType = field.getType();

                    try {
                        Method method = fieldType.getMethod("get", String.class);
                        return (Enum) method.invoke((Object) null, value);
                    } catch (SecurityException | IllegalAccessException | InvocationTargetException | NoSuchMethodException var9) {
                        log.warn("解析枚举失败", var9);
                        return null;
                    }
                }
            }
        } else {
            return null;
        }
    }
}
