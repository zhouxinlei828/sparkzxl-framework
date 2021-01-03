package com.github.sparkzxl.core.entity;

import lombok.Data;

/**
 * description: 验证码信息
 *
 * @author: zhouxinlei
 * @date: 2020-12-26 22:41:02
 */
@Data
public class CaptchaInfo {

    /**
     * key
     */
    private String key;

    /**
     * 验证码
     */
    private String data;

}
