package com.github.sparkzxl.database.mybatis.injector;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.github.sparkzxl.constant.EntityConstant;
import com.github.sparkzxl.database.mybatis.method.UpdateAllById;

import java.util.List;

/**
 * description: 自定义Sql注入
 *
 * @author zhouxinlei
 */
public class BaseSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        /*
          以下 3 个为内置选装件
          头 2 个支持字段筛选函数
         */
        // 例: 不要指定了 update 填充的字段
        methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
        methodList.add(new AlwaysUpdateSomeColumnById());
        methodList.add(new UpdateAllById(field -> !ArrayUtil.containsAny(new String[] {
                EntityConstant.COLUMN_CREATE_TIME, EntityConstant.COLUMN_CREATE_USER,
                EntityConstant.COLUMN_CREATE_USER_ID, EntityConstant.COLUMN_CREATE_USER_NAME
        }, field.getColumn())));
        return methodList;
    }
}
