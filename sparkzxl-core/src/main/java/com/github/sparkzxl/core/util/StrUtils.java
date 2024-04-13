package com.github.sparkzxl.core.util;

import cn.hutool.core.convert.Convert;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * description: 字符串工具类
 *
 * @author zhouxinlei
 * @since 2023-09-05 17:03:34
 */
public class StrUtils {

    public static List<Long> strToLongList(String data) {
        if (StringUtils.isNotEmpty(data)) {
            String[] str = StringUtils.split(data, "|");
            Long[] strArrNum = Convert.toLongArray(str);
            return Arrays.asList(strArrNum);
        } else {
            return emptyList();
        }
    }

    public static List<String> strToList(String data) {
        if (StringUtils.isNotEmpty(data)) {
            String[] str = StringUtils.split(data, "|");
            return Arrays.asList(str);
        } else {
            return emptyList();
        }
    }

    public static String listToString(List<String> list) {
        String str = "";
        if (CollectionUtils.isNotEmpty(list)) {
            str = StringUtils.join(list, "|");
        }
        return str;
    }

    public static String longListToString(List<Long> list) {
        String str = "";
        if (CollectionUtils.isNotEmpty(list)) {
            str = StringUtils.join(list, "|");
        }
        return str;
    }

    public static String listToStringName(List<String> list) {
        String str = "";
        if (CollectionUtils.isNotEmpty(list)) {
            str = StringUtils.join(list, " ");
        }
        return str;
    }

    /**
     * 移除末尾的0，如果字符串中有中间的0，它们将不会被移除
     *
     * @param str 字符串
     * @return String
     */
    public static String removeTrailingZeros(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // 从字符串末尾开始查找第一个非0字符的位置
        int index = str.length() - 1;
        while (index >= 0 && str.charAt(index) == '0') {
            index--;
        }
        // 如果末尾都是0，则返回""，否则返回截取后的字符串
        return index == 0 ? "" : str.substring(0, index + 1);
    }
}
