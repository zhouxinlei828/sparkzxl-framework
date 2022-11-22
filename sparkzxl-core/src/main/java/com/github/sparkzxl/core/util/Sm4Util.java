package com.github.sparkzxl.core.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

/**
 * description: Sm4Util
 *
 * @author zhouxinlei
 * @since 2022-11-22 16:48:24
 */
public class Sm4Util {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final String ALGORITHM_NAME = "SM4";
    public static final String DEFAULT_KEY = "random_seed";

    /**
     * 128-32位16进制；256-64位16进制
     */
    public static final int DEFAULT_KEY_SIZE = 128;

    public static byte[] generateKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        return generateKey(DEFAULT_KEY, DEFAULT_KEY_SIZE);
    }

    public static byte[] generateKey(String seed) throws NoSuchAlgorithmException, NoSuchProviderException {
        return generateKey(seed, DEFAULT_KEY_SIZE);
    }

    public static byte[] generateKey(String seed, int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        if (null != seed && !"".equals(seed)) {
            random.setSeed(seed.getBytes());
        }
        kg.init(keySize, random);
        return kg.generateKey().getEncoded();
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
