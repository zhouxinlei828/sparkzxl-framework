package com.github.sparkzxl.core.utils;

import java.util.Random;

/**
 * description: 随机工具类
 *
 * @author zhouxinlei
 */
public class RandomTool {

    public static String randomNumber(int length) {
        String sources = "0123456789";
        Random rand = new Random();
        StringBuilder flag = new StringBuilder();
        for (int j = 0; j < length; j++) {
            flag.append(sources.charAt(rand.nextInt(9)));
        }
        return flag.toString();
    }

    public static String randomStr(int length) {
        String sources = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder flag = new StringBuilder();
        for (int j = 0; j < length; j++) {
            int number = rand.nextInt(sources.length());
            //根据索引值获取对应的字符
            char charAt = sources.charAt(number);
            flag.append(charAt);
        }
        return flag.toString();
    }
}
