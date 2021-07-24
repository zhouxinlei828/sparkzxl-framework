package com.github.sparkzxl.log.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: 用户行为信息
 *
 * @author zhouxinlei
 */
@Data
public class UserBehaviorLogInfo {

    private String id;

    private String businessType;

    private String userId;

    private String userName;

    private String content;

    private LocalDateTime createTime;

}
