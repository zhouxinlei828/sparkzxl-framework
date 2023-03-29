package com.github.sparkzxl.log.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description: 操作日志实体类
 *
 * @author zhouxinlei
 */
@Data
@Accessors(chain = true)
public class OptLogRecordDetail implements Serializable {

    private static final long serialVersionUID = -7899035779553703264L;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 请求接口
     */
    private String requestUrl;

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

    /**
     * 租户
     */
    private String tenantId;

}
