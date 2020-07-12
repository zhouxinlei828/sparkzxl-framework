package com.sparksys.commons.core.utils.crypto;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;

/**
 * description: AES加密
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:51:00
 */
@Slf4j
public class AesUtils {

    private static AES aes;

    private AesUtils() {

    }

    static {
        String key = "0CoJUm6Qyw8W8jud";
        String iv = "0102030405060708";
        aes = new AES(Mode.CTS, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
    }

    public static String encryptHex(String content) {
        return aes.encryptHex(content, CharsetUtil.CHARSET_UTF_8);
    }

    public static String decryptStr(String content) {
        return aes.decryptStr(content, CharsetUtil.CHARSET_UTF_8);
    }

    public static void main(String[] args) {
        String content = "123456";
        String encryptHex = AesUtils.encryptHex(content);
        System.out.println(encryptHex);
        System.out.println(AesUtils.decryptStr(encryptHex));
        System.out.println(md5("123456"));
    }

    public static String md5(String str) {
        return SecureUtil.md5(str).toUpperCase();
    }


}
