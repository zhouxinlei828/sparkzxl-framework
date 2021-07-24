package com.github.sparkzxl.core.utils;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * description: 短链生成工具
 *
 * @author charles.zhou
 */
public class ShortUrlTool {

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SCALE = 62;

    /**
     * 数字转62进制
     *
     * @param num 数字
     * @return String
     */
    public static String encode(long num) {
        StringBuilder sb = new StringBuilder();
        int remainder;
        while (num > SCALE - 1) {
            //对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转字符串
            remainder = Long.valueOf(num % SCALE).intValue();
            sb.append(CHARS.charAt(remainder));
            //除以进制数，获取下一个末尾数
            num = num / SCALE;
        }
        sb.append(CHARS.charAt(Long.valueOf(num).intValue()));
        String value = sb.reverse().toString();
        int minLength = 5;
        return StringUtils.leftPad(value, minLength, '0');
    }

    /**
     * 62进制转为数字
     *
     * @param str 字符串
     * @return long
     */
    public static long decode(String str) {
        //将 0 开头的字符串进行替换
        str = str.replace("^0*", "");
        long value = 0;
        char tempChar;
        int tempCharValue;
        for (int i = 0; i < str.length(); i++) {
            //获取字符
            tempChar = str.charAt(i);
            //单字符值
            tempCharValue = CHARS.indexOf(tempChar);
            //单字符值在进制规则下表示的值
            value += (long) (tempCharValue * Math.pow(SCALE, str.length() - i - 1));
        }
        return value;
    }

    /**
     * 生成短链
     *
     * @param url url
     * @return String
     */
    public static String generate(String url) {
        return encode(Hashing.murmur3_128().newHasher().putString(url, StandardCharsets.UTF_8).hash().asLong());
    }
}
