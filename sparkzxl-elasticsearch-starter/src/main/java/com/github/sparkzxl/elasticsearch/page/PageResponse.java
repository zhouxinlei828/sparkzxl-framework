package com.github.sparkzxl.elasticsearch.page;

import lombok.Data;

import java.util.List;

/**
 * description: es分页实体类
 *
 * @author zhouxinlei
 * @date 2021-04-17 13:48:05
 */
@Data
public class PageResponse<T> {

    private int pageNum;
    private int pageSize;
    protected long total;
    protected List<T> list;
}
