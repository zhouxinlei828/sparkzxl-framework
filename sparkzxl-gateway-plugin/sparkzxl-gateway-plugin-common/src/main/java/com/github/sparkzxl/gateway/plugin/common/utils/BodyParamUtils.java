package com.github.sparkzxl.gateway.plugin.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.sparkzxl.core.util.ReflectionUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.LinkedMultiValueMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: Common rpc parameter builder utils.
 *
 * @author zhouxinlei
 * @since 2022-08-12 17:17:04
 */
public final class BodyParamUtils {

    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

    private BodyParamUtils() {
    }

    /**
     * buildBodyParams.
     *
     * @param param param.
     * @return the string change to linkedMultiValueMap.
     */
    public static LinkedMultiValueMap<String, String> buildBodyParams(final String param) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Matcher matcher = QUERY_PARAM_PATTERN.matcher(param);
        while (matcher.find()) {
            String name = matcher.group(1);
            String eq = matcher.group(2);
            String value = matcher.group(3);
            params.add(name, value != null ? value : (StringUtils.isNotBlank(eq) ? "" : null));
        }
        return params;
    }

    /**
     * build single parameter.
     *
     * @param body           the parameter body.
     * @param parameterTypes the parameter types.
     * @return the parameters.
     */
    public static Pair<String[], Object[]> buildSingleParameter(final String body, final String parameterTypes) {
        final Map<String, Object> paramMap = JSONObject.parseObject(body, new TypeReference<Map<String, Object>>() {});
        for (String key : paramMap.keySet()) {
            Object obj = paramMap.get(key);
            if (obj instanceof JSONObject) {
                paramMap.put(key, Convert.toMap(String.class, Object.class, obj));
            } else if (obj instanceof JSONArray) {
                paramMap.put(key, Convert.convert(new TypeReference<List<Object>>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                }, obj));
            } else {
                paramMap.put(key, obj);
            }
        }
        return new ImmutablePair<>(new String[]{parameterTypes}, new Object[]{paramMap});
    }


    /**
     * build multi parameters.
     *
     * @param body           the parameter body.
     * @param parameterTypes the parameter types.
     * @return the parameters.
     */
    public static Pair<String[], Object[]> buildParameters(final String body, final String parameterTypes) {
        List<String> paramNameList = new ArrayList<>();
        List<String> paramTypeList = new ArrayList<>();

        if (isNameMapping(parameterTypes)) {
            Map<String, String> paramNameMap = JSONObject.parseObject(parameterTypes,
                    new TypeReference<Map<String, String>>() {});
            paramNameList.addAll(paramNameMap.keySet());
            paramTypeList.addAll(paramNameMap.values());
        } else {
            Map<String, Object> paramMap = JSONObject.parseObject(body,
                    new TypeReference<Map<String, Object>>() {});
            paramNameList.addAll(paramMap.keySet());
            paramTypeList.addAll(Arrays.asList(StringUtils.split(parameterTypes, ",")));
        }

        if (paramTypeList.size() == 1 && !isBaseType(paramTypeList.get(0))) {
            return buildSingleParameter(body, parameterTypes);
        }
        Map<String, Object> paramMap = JSONObject.parseObject(body,
                new TypeReference<Map<String, Object>>() {});
        Object[] objects = paramNameList.stream().map(key -> {
            Object obj = paramMap.get(key);
            if (obj instanceof JSONObject) {
                return Convert.toMap(String.class, Object.class, obj);
            } else if (obj instanceof JSONArray) {
                return Convert.convert(new TypeReference<List<Object>>() {}, obj);
            } else {
                return obj;
            }
        }).toArray();
        String[] paramTypes = paramTypeList.toArray(new String[0]);
        return new ImmutablePair<>(paramTypes, objects);
    }

    private static boolean isNameMapping(final String parameterTypes) {
        return parameterTypes.startsWith("{") && parameterTypes.endsWith("}");
    }

    /**
     * isBaseType.
     *
     * @param paramType the parameter type.
     * @return whether the base type is.
     */
    private static boolean isBaseType(final String paramType) {
        try {
            return ReflectionUtil.isPrimitives(ClassUtils.getClass(paramType));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
