package com.sparksys.database.dto;

import lombok.Getter;

/**
 * description: 分页入参
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:21:22
 */
@Getter
public class PageDTO {

    /**
     * 当前页
     */
    private final Integer pageNum;

    /**
     * 每页显示条数
     */
    private final Integer pageSize;

    public PageDTO(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum == null ? 1 : pageNum;
        this.pageSize = pageSize == null ? 10 : pageSize;
    }
}
