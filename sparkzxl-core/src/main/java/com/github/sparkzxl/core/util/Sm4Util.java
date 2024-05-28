package com.github.sparkzxl.core.util;

import cn.hutool.core.util.RandomUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;
import java.util.Base64;

/**
 * description: Sm4Util
 *
 * @author zhouxinlei
 * @since 2022-11-22 16:48:24
 */
public class Sm4Util {

    public static final String ALGORITHM_NAME = "SM4";
    public static final String DEFAULT_KEY = "random_seed";
    /**
     * 128-32位16进制；256-64位16进制
     */
    public static final int DEFAULT_KEY_SIZE = 16;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成加密key
     *
     * @return String
     */
    public static String generateKey() {
        return RandomUtil.randomString(DEFAULT_KEY_SIZE);
    }

    /**
     * 生成加密key
     *
     * @param size 字符串的长度
     * @return String
     */
    public static String generateKey(final int size) {
        return RandomUtil.randomString(size);
    }

    /**
     * 生成加密向量
     *
     * @return String
     */
    public static String generateIv() {
        return RandomUtil.randomString(DEFAULT_KEY_SIZE);
    }

    /**
     * 生成加密向量
     *
     * @param size 字符串的长度
     * @return String
     */
    public static String generateIv(final int size) {
        return RandomUtil.randomString(size);
    }

    /**
     * 加密
     *
     * @param algorithmName 算法名称
     * @param key           加密key
     * @param iv            加密向量
     * @param data          家居数据
     * @return byte[]
     * @throws Exception 异常
     */
    public static byte[] encrypt(String algorithmName, byte[] key, byte[] iv, byte[] data) throws Exception {
        return sm4core(algorithmName, Cipher.ENCRYPT_MODE, key, iv, data);
    }

    /**
     * 加密
     *
     * @param algorithmName 算法名称
     * @param key           加密key
     * @param iv            加密向量
     * @param data          家居数据
     * @return String Base64String
     * @throws Exception 异常
     */
    public static String encryptToBase64String(String algorithmName, byte[] key, byte[] iv, byte[] data) throws Exception {
        byte[] bytes = sm4core(algorithmName, Cipher.ENCRYPT_MODE, key, iv, data);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 解密
     *
     * @param algorithmName 算法名称
     * @param key           加密key
     * @param iv            加密向量
     * @param data          加密数据
     * @return byte[]
     * @throws Exception 异常
     */
    public static byte[] decrypt(String algorithmName, byte[] key, byte[] iv, byte[] data) throws Exception {
        return sm4core(algorithmName, Cipher.DECRYPT_MODE, key, iv, data);
    }

    /**
     * 解密
     *
     * @param algorithmName 算法名称
     * @param key           加密key
     * @param iv            加密向量
     * @param base64String  加密数据base64
     * @return String
     * @throws Exception 异常
     */
    public static byte[] decryptFromBase64String(String algorithmName, byte[] key, byte[] iv, String base64String) throws Exception {
        byte[] decode = Base64.getDecoder().decode(base64String);
        return sm4core(algorithmName, Cipher.DECRYPT_MODE, key, iv, decode);
    }

    private static byte[] sm4core(String algorithmName, int type, byte[] key, byte[] iv, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        if (algorithmName.contains("/ECB/")) {
            cipher.init(type, sm4Key);
        } else {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(type, sm4Key, ivParameterSpec);
        }

        return cipher.doFinal(data);
    }
}
