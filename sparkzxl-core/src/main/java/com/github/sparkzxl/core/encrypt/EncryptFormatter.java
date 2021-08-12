package com.github.sparkzxl.core.encrypt;

public interface EncryptFormatter {

    /**
     * @param source 加密数据
     * @return String
     */
    String encrypted(Object source);

}
