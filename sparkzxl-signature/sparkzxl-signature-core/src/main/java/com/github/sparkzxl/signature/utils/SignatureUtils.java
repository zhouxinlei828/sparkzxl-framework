package com.github.sparkzxl.signature.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;

public class SignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignatureUtils.class);

    /**
     * 生成签名sign参数数据
     *
     * @param params 请求参数 。注意请求参数中不能包含key
     * @return String
     */
    private static String generateSignData(Map<String, Object> params, String secret) {
        // 第1步: 将所有参数（注意是所有参数，包括appKey,timestamp,nonce），除去sign本身,拼接成字符串
        String mapToString = SortUtils.mapToString(params, "&", "=");
        // 第2步: 将参数名和值的拼接
        String signData = mapToString.replaceAll("&", "").replaceAll("=", "");
        System.out.println(signData);
        // 第2步: 在上面拼接得到的字符串前加上密钥secret
        return signData + secret;
    }

    /**
     * 生成签名sign(HmacSHA256)
     *
     * @param params 请求参数
     * @return String
     */
    public static String generateSignHmacSHA256(Map<String, Object> params, String secret) {
        String signData = generateSignData(params, secret);
        //签名
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, secret.getBytes());
        return mac.digestHex(signData);
    }

    /**
     * 校验签名sign(HmacSHA256)
     *
     * @param secret 秘钥
     * @param params 请求参数
     * @param sign   签名
     * @return boolean
     */
    public static boolean verifySignHmacSHA256(Map<String, Object> params, String secret, String sign) {
        //签名
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, secret.getBytes());
        String signHmacSHA256 = generateSignHmacSHA256(params, secret);
        return mac.verify(sign.getBytes(), signHmacSHA256.getBytes());
    }


    /**
     * 生成签名(RSA)
     * 使用私钥加密，公钥解密.用于让所有公钥所有者验证私钥所有者的身份并且用来防止私钥所有者发布的内容被篡改，但是不用来保证内容不被他人获得。
     *
     * @param params     请求参数
     * @param privateKey 私钥
     * @return String
     */
    public static String generateRsaSign(Map<String, Object> params, String privateKey) {
        //签名规则
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, privateKey, null);
        String signData = generateSignData(params, null);
        byte[] data = Convert.toStr(signData).getBytes();
        //签名
        byte[] signed = sign.sign(data);
        return Base64.getEncoder().encodeToString(signed);
    }

    /**
     * 验签(RSA)
     *
     * @param params    请求参数
     * @param signData  sign 签名
     * @param publicKey publicKey RSA公钥
     * @return boolean
     */
    public static Boolean verifyRsaSign(Map<String, Object> params, String signData, String publicKey) {
        //签名规则
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, publicKey);
        //参数值
        String param = generateSignData(params, null);
        //将String转换为byte
        byte[] data = Convert.toStr(param).getBytes();
        //验证签名
        //返回
        return sign.verify(data, Base64.getDecoder().decode(signData));
    }

    public static void main(String[] args) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appKey", "111111111111");
        params.put("timestamp", 1720142063492L);
        params.put("nonce", "22222222");
        params.put("k1", "zs001");
        params.put("k2", "zs002");
        params.put("k3", "zs003");
        params.put("method", "cancel");
        String secret = IdUtil.fastSimpleUUID();
        String sign = generateSignHmacSHA256(params, secret);
        System.out.println("sign:" + sign);
        System.out.println(verifySignHmacSHA256(params, secret, sign));
        System.out.println(IdUtil.getSnowflakeNextId());
    }

}
