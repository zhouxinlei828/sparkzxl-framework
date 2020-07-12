package com.sparksys.commons.core.utils.common;

import cn.hutool.core.util.RandomUtil;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;

import java.text.DecimalFormat;

/**
 * description: 数字转换工具
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:50:33
 */
public class NumberUtil extends NumberUtils {

    private NumberUtil() {

    }

    public static int toInt(final String str) {
        return toInt(str, -1);
    }

    public static int toInt(@Nullable final String str, final int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static long toLong(final String str) {
        return toLong(str, 0L);
    }

    public static long toLong(@Nullable final String str, final long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Double toDouble(String value) {
        return toDouble(value, null);
    }

    public static Double toDouble(@Nullable String value, Double defaultValue) {
        if (value != null) {
            return Double.valueOf(value.trim());
        }
        return defaultValue;
    }

    public static Float toFloat(String value) {
        return toFloat(value, null);
    }

    public static Float toFloat(@Nullable String value, Float defaultValue) {
        if (value != null) {
            return Float.valueOf(value.trim());
        }
        return defaultValue;
    }

    private final static char[] DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z'
    };

    public static String to62String(long i) {
        int radix = DIGITS.length;
        char[] buf = new char[65];
        int charPos = 64;
        i = -i;
        while (i <= -radix) {
            buf[charPos--] = DIGITS[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = DIGITS[(int) (-i)];

        return new String(buf, charPos, (65 - charPos));
    }

    public static double completion(int personCount, int unfinishedCount) {
        if (personCount == 0) {
            return 0.0;
        }
        int finishedCount = personCount - unfinishedCount;
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        return Double.parseDouble(decimalFormat.format(finishedCount / (double) personCount)) * 100;
    }


    public static String getRandom(int length) {
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < length; i++) {
            val.append(RandomUtil.randomLong(10));
        }
        return val.toString();
    }
}
