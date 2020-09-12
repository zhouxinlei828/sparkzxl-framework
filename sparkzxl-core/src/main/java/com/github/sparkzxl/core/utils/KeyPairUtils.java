package com.github.sparkzxl.core.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * description: 加载读取资源文件
 *
 * @author: zhouxinlei
 * @date: 2020-07-15 20:25:16
 */
public class KeyPairUtils {

    public static KeyPair keyPair(String path, String alias, String password) {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(path),
                password.toCharArray());
        return keyStoreKeyFactory.getKeyPair(alias, password.toCharArray());
    }
}
