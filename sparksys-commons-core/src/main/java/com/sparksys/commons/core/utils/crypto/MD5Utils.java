package com.sparksys.commons.core.utils.crypto;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.digest.MD5;

/**
 * description: MD5加密
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:51:43
 */
public class MD5Utils {

    public static String encrypt(String str) {
        MD5 md5 = new MD5();
        return md5.digestHex(str, CharsetUtil.CHARSET_UTF_8).toUpperCase();
    }

    public static void main(String[] args) {
        System.out.println(MD5Utils.encrypt("123456"));
    }
}
