package com.sparksys.commons.core.utils.crypto;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.system.SystemUtil;

/**
 * description: RSA非对称加密
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:51:55
 */
public class RsaUtils {

    private static RSA rsa;
    private static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMR2YL+aP6Amc/n+SlMFr7Tq" +
            "+DXWwYOr6pNiBOFty21NnaesGq7gABnLNYVPh4ImCL9MNWOH+lQBYkYDYNYvqe2GL/SfoYHWnCXBC7TEJIvtwCdnEE6Sf8fIENjfEJMLlKxtgYlUOKIqf60OcpZ9+5+779pMsLvxHtzcdes5KFXPAgMBAAECgYB12ZEFvrCmEAsIDotlLAcPntfP7AhfRPRM/sJwu90Ir0OAPlQvTL2iu2xakK932frrTfrEvt/iHoZl+0bOlP74KKrQCWSyT4NUKzxZ/LEPO93ZzxQZGfIl0jXwyJZUWfxP+PwdDPRAsvUCc2VTorR6TmjMFr3c3bnKt2IXSsyyYQJBAOQWFl9xXl8N0Nh9p4rDpn6atUYDKht/XHau4Ia3ylLSNI7uGs2y4vwPzaJDXtFEZuERd3LlALxd0QWRwe56Vp8CQQDcgYThgfLf0cU25ud+hwYGwsslfIu8YI4Wu2qyM/XDFvCrLWkWcEyPAMPa9x/QqQu1h3TR5169mJyBXbGjKaLRAkB56tCErd2q44Cp5+ihhNk/PUT+Knzce+SZz/pWKHGueYv8houvPhqUb4IUZPyO0YhIcASwVnZhOaib2ICYD66zAkEAw/wWJ/zA6q/eclqufGX0NEQkHNBivm6vO4MeZP8Lu3cDN5Gn6nqrgRqz+UVWMLZfItwfN4VGTot65vxoq1WRUQJAVR2pcWILVpccTXFH3UOHhhGcvfi60S42uUNvajRud8StzrE5NN6Dr22Twb4R2fex3WBBWmU1eW5Zx8eloMDbQw==";
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEdmC/mj+gJnP5/kpTBa+06vg11sGDq+qTYgThbcttTZ2nrBqu4AAZyzWFT4eCJgi/TDVjh/pUAWJGA2DWL6nthi/0n6GB1pwlwQu0xCSL7cAnZxBOkn/HyBDY3xCTC5SsbYGJVDiiKn+tDnKWffufu+/aTLC78R7c3HXrOShVzwIDAQAB";

    static {
        rsa = new RSA(privateKey, publicKey);
    }

    public static String encryptHex(String content) {
        return rsa.encryptHex(StrUtil.bytes(content, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
    }

    public static String decryptStr(String content) {
        return rsa.decryptStr(content, KeyType.PrivateKey);
    }


    public static void main(String[] args) {
        String content = "Z9KzpZ5da2DWEUHmto1ksB2NChTRG9";
        String encryptHex = RsaUtils.encryptHex(content);
        System.out.println(encryptHex);
        System.out.println(RsaUtils.decryptStr(encryptHex));
    }

}
