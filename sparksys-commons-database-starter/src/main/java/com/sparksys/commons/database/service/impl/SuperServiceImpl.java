package com.sparksys.commons.database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sparksys.commons.database.mapper.SuperMapper;
import com.sparksys.commons.database.service.SuperService;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-07-07 20:14:55
 */
public class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {
    public SuperServiceImpl() {
    }

    @Override
    public boolean save(T model) {
        return super.save(model);
    }

    @Override
    public boolean updateById(T model) {
        return super.updateById(model);
    }
}
