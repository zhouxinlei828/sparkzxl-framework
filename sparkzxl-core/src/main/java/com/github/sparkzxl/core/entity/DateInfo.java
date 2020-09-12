package com.github.sparkzxl.core.entity;

import lombok.Data;

import java.util.Date;


/**
 * description: 开始结束时间通用实体类
 *
 * @author: zhouxinlei
 * @date: 2020-08-14 09:19:59
 */
@Data
public class DateInfo {

    private Date startDate;

    private String startTime;

    private Date endDate;

    private String endTime;

}
