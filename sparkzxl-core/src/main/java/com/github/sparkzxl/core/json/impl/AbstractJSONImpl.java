package com.github.sparkzxl.core.json.impl;

import com.github.sparkzxl.core.json.JSON;

import java.util.List;
import java.util.Map;

/**
 * description: AbstractJSONImpl
 *
 * @author zhouxinlei
 * @since 2022-12-30 14:41:35
 */
public abstract class AbstractJSONImpl implements JSON {

    @Override
    public List<?> getList(Map<String, ?> obj, String key) {
        assert obj != null;
        assert key != null;
        if (!obj.containsKey(key)) {
            return null;
        }
        Object value = obj.get(key);
        if (!(value instanceof List)) {
            throw new ClassCastException(String.format("value '%s' for key '%s' in '%s' is not List", value, key, obj));
        }
        return (List<?>) value;
    }
}
