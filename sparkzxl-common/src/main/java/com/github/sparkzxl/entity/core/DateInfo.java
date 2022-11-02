package com.github.sparkzxl.entity.core;

import lombok.Data;

import java.util.Date;


/**
 * description: 开始结束时间通用实体类
 *
 * @author zhouxinlei
 */
@Data
public class DateInfo {

    private Date startDate;

    private String startTime;

    private Date endDate;

    private String endTime;

}
