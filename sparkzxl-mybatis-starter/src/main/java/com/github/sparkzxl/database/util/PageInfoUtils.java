package com.github.sparkzxl.database.util;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * description: PageInfo静态工具类
 *
 * @author zhouxinlei
 */
public class PageInfoUtils {

    public static <T> PageInfo<T> pageInfo(List<T> list) {
        return new PageInfo<>(list);
    }
}
