package com.github.sparkzxl.signature.constant.enums;

import lombok.Getter;

@Getter
public enum AlgorithmEnum {

    HmacMD5("HmacMD5"),
    HmacSHA256("HmacSHA256"),

    SHA256withRSA("SHA256withRSA"),
    // The DSA with SHA-1 signature algorithm
    SHA1withDSA("SHA1withDSA"),
    SHA256withECDSA("SHA256withECDSA"),
    // 需要BC库加入支持
    SHA256withRSA_PSS("SHA256WithRSA/PSS"),
    ;

    private final String value;

    AlgorithmEnum(String value) {
        this.value = value;
    }
}
