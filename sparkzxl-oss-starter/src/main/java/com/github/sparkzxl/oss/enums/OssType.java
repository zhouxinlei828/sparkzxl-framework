package com.github.sparkzxl.oss.enums;

import lombok.Getter;

/**
 * description: oss 类型
 *
 * @author zhouxinlei
 * @since 2022-10-12 09:02:59
 */
@Getter
public enum OssType {

    MINIO("minio"),
    ALIYUN("aliyun"),
    ;

    private String type;

    OssType(String type) {
        this.type = type;
    }
}
