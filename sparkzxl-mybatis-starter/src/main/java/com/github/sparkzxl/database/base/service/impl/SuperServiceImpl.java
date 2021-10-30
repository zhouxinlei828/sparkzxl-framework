package com.github.sparkzxl.database.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.sparkzxl.database.base.mapper.SuperMapper;
import com.github.sparkzxl.database.base.service.SuperService;

/**
 * description:
 *
 * @author zhouxinlei
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
