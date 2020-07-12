package com.sparksys.commons.mybatis.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    public <T> IPage<T> startPage() {
        return new Page<>(pageNum, pageSize);
    }

}
