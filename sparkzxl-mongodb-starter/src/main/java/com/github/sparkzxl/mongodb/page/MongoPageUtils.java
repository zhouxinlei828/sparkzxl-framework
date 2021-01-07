package com.github.sparkzxl.mongodb.page;

import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2021-01-07 12:39:50
 */
public class MongoPageUtils {

    public static <T> PageInfo<T> pageInfo(List<T> items, Long total, Pageable pageable) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(pageable.getPageNumber());
        pageInfo.setPageSize(pageable.getPageSize());
        pageInfo.setList(items);
        pageInfo.setTotal(total);
        return pageInfo;
    }
}
