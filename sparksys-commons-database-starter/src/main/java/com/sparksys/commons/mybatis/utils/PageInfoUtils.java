package com.sparksys.commons.mybatis.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * description: PageInfo静态工具类
 *
 * @author: zhouxinlei
 * @date: 2020-07-11 22:56:51
 */
public class PageInfoUtils {

    public static <T> PageInfo<T> pageInfo(List<T> list) {
        return new PageInfo<>(list);
    }
}
