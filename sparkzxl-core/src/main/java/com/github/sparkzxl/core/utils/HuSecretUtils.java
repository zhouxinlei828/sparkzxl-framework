package com.github.sparkzxl.core.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * description: hutool 密钥工具类
 *
 * @author zhouxinlei
 */
public class HuSecretUtils {

    private static final AES AES;

    private static final DES DES;

    public static RSA rsa;
    public static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMR2YL+aP6Amc/n+SlMFr7Tq" +
            "+DXWwYOr6pNiBOFty21NnaesGq7gABnLNYVPh4ImCL9MNWOH+lQBYkYDYNYvqe2GL/SfoYHWnCXBC7TEJIvtwCdnEE6Sf8fIENjfEJMLlKxtgYlUOKIqf60OcpZ9+5+779pMsLvxHtzcdes5KFXPAgMBAAECgYB12ZEFvrCmEAsIDotlLAcPntfP7AhfRPRM/sJwu90Ir0OAPlQvTL2iu2xakK932frrTfrEvt/iHoZl+0bOlP74KKrQCWSyT4NUKzxZ/LEPO93ZzxQZGfIl0jXwyJZUWfxP+PwdDPRAsvUCc2VTorR6TmjMFr3c3bnKt2IXSsyyYQJBAOQWFl9xXl8N0Nh9p4rDpn6atUYDKht/XHau4Ia3ylLSNI7uGs2y4vwPzaJDXtFEZuERd3LlALxd0QWRwe56Vp8CQQDcgYThgfLf0cU25ud+hwYGwsslfIu8YI4Wu2qyM/XDFvCrLWkWcEyPAMPa9x/QqQu1h3TR5169mJyBXbGjKaLRAkB56tCErd2q44Cp5+ihhNk/PUT+Knzce+SZz/pWKHGueYv8houvPhqUb4IUZPyO0YhIcASwVnZhOaib2ICYD66zAkEAw/wWJ/zA6q/eclqufGX0NEQkHNBivm6vO4MeZP8Lu3cDN5Gn6nqrgRqz+UVWMLZfItwfN4VGTot65vxoq1WRUQJAVR2pcWILVpccTXFH3UOHhhGcvfi60S42uUNvajRud8StzrE5NN6Dr22Twb4R2fex3WBBWmU1eW5Zx8eloMDbQw==";
    public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEdmC/mj+gJnP5/kpTBa+06vg11sGDq+qTYgThbcttTZ2nrBqu4AAZyzWFT4eCJgi/TDVjh/pUAWJGA2DWL6nthi/0n6GB1pwlwQu0xCSL7cAnZxBOkn/HyBDY3xCTC5SsbYGJVDiiKn+tDnKWffufu+/aTLC78R7c3HXrOShVzwIDAQAB";


    private HuSecretUtils() {

    }

    static {
        DES = new DES(Mode.CTS, Padding.PKCS5Padding, "0CoJUm6Qyw8W8jud".getBytes(), "01020304".getBytes());
        AES = new AES(Mode.CTS, Padding.PKCS5Padding, "0CoJUm6Qyw8W8jud".getBytes(), "0102030405060708".getBytes());
        rsa = new RSA(privateKey, publicKey);
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
        return SecureUtil.md5(str);
    }

    public static String encryptRsaHex(String content) {
        return rsa.encryptHex(StrUtil.bytes(content, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
    }

    public static String decryptRsaStr(String content) {
        return rsa.decryptStr(content, KeyType.PrivateKey);
    }

    public static PrivateKey getPrivateKey() {
        return rsa.getPrivateKey();
    }

    public static PublicKey getPublicKey() {
        return rsa.getPublicKey();
    }

}
