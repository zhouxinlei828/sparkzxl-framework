package com.github.sparkzxl.feign.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * description: 谓词结果信息
 *
 * @author zhouxinlei
 * @since 2022-05-09 17:01:10
 */
@AllArgsConstructor
@Data
public class PredicateMessage {

    /**
     * 详细异常码
     */
    private String errorCode;

    /**
     * 详细错误信息
     */
    private String errorMessage;

    public static PredicateMessage convert(String errorCode, String errorMessage) {
        return new PredicateMessage(errorCode,errorMessage);
    }
}
