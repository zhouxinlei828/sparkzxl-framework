package com.sparksys.commons.mybatis.injector;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteByIdWithFill;
import com.sparksys.commons.mybatis.methods.DeleteAll;

import java.util.List;

/**
 * description: 自定义Sql注入
 *
 * @author: zhouxinlei
 * @date: 2020-07-11 16:54:45
 */
public class BaseSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        //增加自定义方法
        methodList.add(new DeleteAll());
        /**
         * 以下 3 个为内置选装件
         * 头 2 个支持字段筛选函数
         */
        // 例: 不要指定了 update 填充的字段
        methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
        methodList.add(new AlwaysUpdateSomeColumnById());
        methodList.add(new LogicDeleteByIdWithFill());
        return methodList;
    }
}