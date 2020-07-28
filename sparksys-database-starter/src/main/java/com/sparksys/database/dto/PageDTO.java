package com.sparksys.database.dto;

import lombok.Data;

/**
 * description: 分页入参
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:21:22
 */
@Data
public class PageDTO {
    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    private Integer pageSize = 10;

}
