package com.github.sparkzxl.log.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * description: 操作日志实体类
 *
 * @author zhouxinlei
 * @date 2021-09-22 10:59:46
 */
@Data
@Accessors(chain = true)
public class OptLogRecordDetail implements Serializable {

    /**
     * 业务对象标识
     */
    private String bizNo;

    /**
     * 操作日志的种类
     */
    private String category;

    /**
     * 日志详情
     */
    private String detail;

    /**
     * 操作人id
     */
    private String userId;

    /**
     * 操作人
     */
    private String operator;

}
