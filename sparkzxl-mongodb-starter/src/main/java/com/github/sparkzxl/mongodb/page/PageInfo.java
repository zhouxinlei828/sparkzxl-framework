package com.github.sparkzxl.mongodb.page;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * description: 分页工具类
 *
 * @author: zhouxinlei
 * @date: 2021-01-07 12:37:43
 */
@Data
public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 5994888304969019032L;

    private int pageNum;

    private int pageSize;

    private int size;

    private int pages;

    protected long total;

    protected List<T> list;


}
