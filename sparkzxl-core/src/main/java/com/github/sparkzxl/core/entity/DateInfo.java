package com.github.sparkzxl.core.entity;

import java.util.Date;
import lombok.Data;


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
