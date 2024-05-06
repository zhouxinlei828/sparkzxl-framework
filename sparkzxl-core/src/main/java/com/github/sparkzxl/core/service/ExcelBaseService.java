package com.github.sparkzxl.core.service;

import java.util.List;

/**
 * description: excel save service
 *
 * @author zhouxinlei
 * @since 2024-05-06 09:08:47
 */
public interface ExcelBaseService<T> {

    boolean save(T entity);

    boolean saveBatch(List<T> entityList);
}
