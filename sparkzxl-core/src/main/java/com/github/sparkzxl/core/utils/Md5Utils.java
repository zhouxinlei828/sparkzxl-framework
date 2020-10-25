package com.github.sparkzxl.core.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.digest.MD5;

/**
 * description: MD5加密
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:51:43
 */
public class Md5Utils {

    public static String encrypt(String str) {
        MD5 md5 = new MD5();
        return md5.digestHex(str, CharsetUtil.CHARSET_UTF_8).toUpperCase();
    }
}
