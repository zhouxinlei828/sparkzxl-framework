package com.github.sparkzxl.core.json.impl;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JacksonImpl extends AbstractJSONImpl {
    @Override
    public boolean isSupport() {
        try {
            Class<?> aClass = Class.forName("com.fasterxml.jackson.databind.json.JsonMapper");
            return aClass != null;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public String toJson(Object val) {
        return null;
    }

    @Override
    public String toJsonPretty(Object val) {
        return null;
    }

    @Override
    public <T> T toJavaObject(String json, Type type) {
        return null;
    }

    @Override
    public <T> T toJavaObject(String json, TypeReference<T> typeReference) {
        return null;
    }

    @Override
    public <T> T toJavaObject(Map val, Type type) {
        return null;
    }

    @Override
    public <T> List<T> toJavaList(String json, Class<T> clazz) {
        return null;
    }

    @Override
    public Map<String, Object> toMap(String json) {
        return null;
    }

    @Override
    public Map<String, Object> toMap(Object val) {
        return null;
    }

    @Override
    public <T> Map<String, T> toMap(String json, Class<T> clazz) {
        return null;
    }

    @Override
    public <T, E> Map<String, T> toMap(E val, Class<T> clazz) {
        return null;
    }
}
