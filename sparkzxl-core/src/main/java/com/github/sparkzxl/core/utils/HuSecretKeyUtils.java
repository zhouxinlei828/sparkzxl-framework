package com.github.sparkzxl.core.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;

import java.nio.charset.Charset;

/**
 * description: 密钥工具类
 *
 * @author: zhouxinlei
 * @date: 2020-10-25 20:26:17
 */
public class HuSecretKeyUtils {

    private static final AES AES;

    private static final DES DES;

    private HuSecretKeyUtils() {

    }

    static {
        String key = "0CoJUm6Qyw8W8jud";
        String iv = "01020304";
        DES = new DES(Mode.CTS, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
    }

    static {
        String key = "0CoJUm6Qyw8W8jud";
        String iv = "0102030405060708";
        AES = new AES(Mode.CTS, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
    }

    public static String encryptAesHex(String content) {
        return AES.encryptHex(content, CharsetUtil.CHARSET_UTF_8);
    }

    public static String decryptAesStr(String content) {
        return AES.decryptStr(content, CharsetUtil.CHARSET_UTF_8);
    }

    public static String encryptDesHex(String content) {
        return DES.encryptHex(content);
    }

    public static String decryptDesStr(String content, Charset charset) {
        return DES.decryptStr(content, charset);
    }

    public static String encryptDes(String content) {
        return new String(DES.encrypt(content));
    }

    public static String decryptDes(String content) {
        return new String(DES.decrypt(content));
    }

    public static String encryptMd5(String str) {
        return SecureUtil.md5(str).toUpperCase();
    }
}
