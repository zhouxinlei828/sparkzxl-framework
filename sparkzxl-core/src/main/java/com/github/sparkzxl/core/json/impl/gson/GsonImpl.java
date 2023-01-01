package com.github.sparkzxl.core.json.impl.gson;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.json.impl.AbstractJSONImpl;
import com.github.sparkzxl.core.json.impl.jackson.JacksonEnhanceModule;
import com.github.sparkzxl.core.support.JwtParseException;
import com.github.sparkzxl.core.util.StrPool;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.*;

/**
 * description: GsonImpl
 *
 * @author zhouxinlei
 * @since 2023-01-01 11:51:03
 */
public class GsonImpl extends AbstractJSONImpl {

    private static final Logger logger = LoggerFactory.getLogger(JacksonEnhanceModule.class);
    public static final String NAME = "gson";
    private static final String DOT = ".";
    private static final String E = "e";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String EMPTY = "";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(String.class, new StringTypeAdapter())
            .registerTypeHierarchyAdapter(Pair.class, new PairTypeAdapter())
            .registerTypeHierarchyAdapter(Duration.class, new DurationTypeAdapter())
            .create();
    private static final Gson GSON_FORMAT = new GsonBuilder()
            .registerTypeAdapter(String.class, new StringTypeAdapter())
            .registerTypeHierarchyAdapter(Pair.class, new PairTypeAdapter())
            .registerTypeHierarchyAdapter(Duration.class, new DurationTypeAdapter())
            .setPrettyPrinting()
            .create();

    private static final Gson GSON_MAP = new GsonBuilder().serializeNulls().registerTypeHierarchyAdapter(new TypeToken<Map<String, Object>>() {
    }.getRawType(), new MapDeserializer<String, Object>()).create();

    @Override
    public String named() {
        return NAME;
    }

    @Override
    public boolean isSupport() {
        try {
            Class<?> aClass = Class.forName("com.google.gson.Gson");
            return aClass != null;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public String toJson(Object val) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return GSON.toJson(val);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public String toJsonPretty(Object val) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return GSON_FORMAT.toJson(val);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> T toJavaObject(String json, Type type) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> T toJavaObject(String json, TypeReference<T> typeReference) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return GSON.fromJson(json, typeReference.getType());
        } catch (JsonSyntaxException e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> T toJavaObject(Object val, Type type) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            String jsonString = GSON.toJson(val);
            return GSON.fromJson(jsonString, type);
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> List<T> toJavaList(String json, Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            String jsonStr = json;
            if (!StrUtil.startWith(json, StrPool.LEFT_SQ_BRACKET)) {
                jsonStr = StrPool.LEFT_SQ_BRACKET + json + StrPool.RIGHT_SQ_BRACKET;
            }
            return GSON.fromJson(jsonStr, TypeToken.getParameterized(List.class, clazz).getType());
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> toMap(String json) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return GSON_MAP.fromJson(json, new TypeToken<LinkedHashMap<String, Object>>() {
            }.getType());
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> toMap(Object val) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            String json = GSON.toJson(val);
            return GSON_MAP.fromJson(json, TypeToken.getParameterized(LinkedHashMap.class, String.class, Object.class).getType());
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T> Map<String, T> toMap(String json, Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return GSON.fromJson(json, TypeToken.getParameterized(LinkedHashMap.class, String.class, clazz).getType());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JwtParseException(e.getMessage());
        }
    }

    @Override
    public <T, E> Map<String, T> toMap(E val, Class<T> clazz) {
        try {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            String json = GSON.toJson(val);
            return GSON_MAP.fromJson(json, TypeToken.getParameterized(Map.class, String.class, clazz).getType());
        } catch (Exception e) {
            throw new JwtParseException(e.getMessage());
        }
    }

    private static class MapDeserializer<T, U> implements JsonDeserializer<Map<T, U>> {
        @SuppressWarnings("unchecked")
        @Override
        public Map<T, U> deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) {
            if (!json.isJsonObject()) {
                return null;
            }
            String className = ((ParameterizedType) type).getRawType().getTypeName();
            Class<Map<?, ?>> mapClass = null;
            try {
                mapClass = (Class<Map<?, ?>>) Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("failed to get class", e);
            }

            Map<T, U> resultMap = null;
            assert mapClass != null;
            if (Objects.requireNonNull(mapClass).isInterface()) {
                resultMap = new LinkedHashMap<>();
            } else {
                try {
                    resultMap = (Map<T, U>) mapClass.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    logger.error("failed to get constructor", e);
                }
            }
            JsonObject jsonObject = json.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> jsonEntrySet = jsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : jsonEntrySet) {
                if (entry.getValue().isJsonNull()) {
                    if (Objects.nonNull(resultMap)) {
                        resultMap.put((T) entry.getKey(), null);
                    }
                } else {
                    U value = context.deserialize(entry.getValue(), this.getType(entry.getValue()));
                    if (Objects.nonNull(resultMap)) {
                        resultMap.put((T) entry.getKey(), value);
                    }
                }
            }
            return resultMap;
        }

        /**
         * Get JsonElement class type.
         *
         * @param element the element
         * @return Class the class
         */
        public Class<?> getType(final JsonElement element) {
            if (!element.isJsonPrimitive()) {
                return element.getClass();
            }

            final JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return String.class;
            }
            if (primitive.isNumber()) {
                String numStr = primitive.getAsString();
                if (numStr.contains(DOT) || numStr.contains(E)
                        || numStr.contains(E.toUpperCase())) {
                    return Double.class;
                }
                return Long.class;
            }
            if (primitive.isBoolean()) {
                return Boolean.class;
            }
            return element.getClass();
        }
    }

    private static class StringTypeAdapter extends TypeAdapter<String> {
        @Override
        public void write(final JsonWriter out, final String value) {
            try {
                if (StringUtils.isBlank(value)) {
                    out.nullValue();
                    return;
                }
                out.value(value);
            } catch (IOException e) {
                logger.error("failed to write", e);
            }
        }

        @Override
        public String read(final JsonReader reader) {
            try {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return EMPTY;
                }
                return reader.nextString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class PairTypeAdapter extends TypeAdapter<Pair<String, String>> {

        @Override
        public void write(final JsonWriter out, final Pair<String, String> value) throws IOException {
            out.beginObject();
            out.name(LEFT).value(value.getLeft());
            out.name(RIGHT).value(value.getRight());
            out.endObject();
        }

        @Override
        public Pair<String, String> read(final JsonReader in) throws IOException {
            in.beginObject();
            String left = null;
            String right = null;
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case LEFT:
                        left = in.nextString();
                        break;
                    case RIGHT:
                        right = in.nextString();
                        break;
                    default:
                        break;
                }
            }
            in.endObject();
            return Pair.of(left, right);
        }
    }

    private static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter out, final Duration value) {
            try {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.value(value.toString());
            } catch (IOException e) {
                logger.error("failed to write", e);
            }
        }

        @Override
        public Duration read(final JsonReader reader) {
            try {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                }
                return Duration.parse(reader.nextString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
