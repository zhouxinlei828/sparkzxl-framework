package com.github.sparkzxl.core.utils;

import cn.hutool.core.util.StrUtil;


/**
 * description: 缓存key前缀
 *
 * @author: zhouxinlei
 * @date: 2020-07-13 14:07:32
 */
public class BuildKeyUtils {

    /**
     * 构建key
     *
     * @param args 参数
     * @return String
     */
    public static String generateKey(String template, Object... args) {
        StringBuilder key = new StringBuilder();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                template = template.concat(":{}");
            }
            key.append(StrUtil.format(template, args));
        }
        return key.toString();
    }

    public static String generateKey(Object... args) {
        if (args.length == 1) {
            return String.valueOf(args[0]);
        } else {
            return args.length > 0 ? StrUtil.join(":", args) : "";
        }
    }

    public static String key(Object... args) {
        return generateKey(args);
    }
}
