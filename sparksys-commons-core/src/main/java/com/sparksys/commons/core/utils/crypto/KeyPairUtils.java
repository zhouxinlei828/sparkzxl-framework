package com.sparksys.commons.core.utils.crypto;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * description: 获取keyPair工具类
 *
 * @author: zhouxinlei
 * @date: 2020-07-15 16:05:02
 */
public class KeyPairUtils {

    public static KeyPair keyPair(String path, String password) {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(path), password.toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }




    public static void main(String[] args) {
        KeyPairUtils.keyPair("D:\\ideaProjects\\sparksys-commons\\jwt.jks","123456");
    }

}
