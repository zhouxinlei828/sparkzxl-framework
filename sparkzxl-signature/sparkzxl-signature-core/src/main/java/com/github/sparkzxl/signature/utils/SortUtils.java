package com.github.sparkzxl.signature.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.StrBuilder;
import com.google.common.collect.Lists;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.util.StrPool;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SortUtils {

    private static final String[] BASE_TYPES = {
            StrPool.INTEGER_TYPE_NAME, StrPool.BYTE_TYPE_NAME, StrPool.LONG_TYPE_NAME, StrPool.DOUBLE_TYPE_NAME,
            StrPool.FLOAT_TYPE_NAME, StrPool.CHARACTER_TYPE_NAME, StrPool.SHORT_TYPE_NAME, StrPool.BOOLEAN_TYPE_NAME, StrPool.STRING_TYPE_NAME
    };

    private static boolean isBaseType(Object object) {
        String typeName = object.getClass().getTypeName();
        for (String baseType : BASE_TYPES) {
            if (baseType.equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按照字母顺序进行升序排序
     *
     * @param params             请求参数 。注意请求参数中不能包含key
     * @param connectSymlinks    连接符号，连接两个key/value字段之间的符号，例如"&"、","号
     * @param assignmentSymlinks 赋值符号,例如"="号
     * @return 排序后结果
     */
    public static String mapToString(Map<String, Object> params, String connectSymlinks, String assignmentSymlinks) {
        List<String> listKeys = Lists.newArrayList(params.keySet());
        Collections.sort(listKeys);
        StrBuilder content = StrBuilder.create();
        for (String key : listKeys) {
            Object data = params.get(key);
            String value;
            if (data instanceof Map) {
                // 递归遍历
                value = mapToString(JsonUtils.getJson().toMap(data), connectSymlinks, assignmentSymlinks);
            } else if (data instanceof List) {
                List<?> list = Convert.toList(data);
                // 递归遍历
                StrBuilder nodeValue = StrBuilder.create();
                for (Object m : list) {
                    if (m instanceof Map) {
                        nodeValue.append(mapToString(JsonUtils.getJson().toMap(m), connectSymlinks, assignmentSymlinks)).append(connectSymlinks);
                    } else if (isBaseType(m)) {
                        // 判断是否是基础类型
                        nodeValue.append(m.toString());
                    } else {
                        nodeValue.append(m.toString());
                    }
                }
                value = nodeValue.toString();
            } else {
                value = Convert.toStr(data);
            }
            // Skip empty values
            if (!StringUtils.hasText(value)) {
                continue;
            }
            content.append(key).append(assignmentSymlinks).append(value).append(connectSymlinks);
        }
        if (!content.isEmpty()) {
            return content.subString(0, content.length() - 1);
        }
        // Remove the last connection symbol if content is not empty
        return content.isEmpty() ? content.toString() : content.subString(0, content.length() - 1);
    }
}
