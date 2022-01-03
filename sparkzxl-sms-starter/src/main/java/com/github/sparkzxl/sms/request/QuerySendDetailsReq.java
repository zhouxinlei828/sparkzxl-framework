package com.github.sparkzxl.sms.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * description: 查询发送详情入参
 *
 * @author zhouxinlei
 * @date 2022-01-03 16:37:56
 */
@Data
@Accessors(chain = true)
public class QuerySendDetailsReq implements Serializable {

    private static final long serialVersionUID = -9080742455415377654L;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 发送日期
     */
    private LocalDateTime sendDate;

    /**
     * 手机号
     */
    private String phoneNumber;

}
