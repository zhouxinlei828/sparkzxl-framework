package com.github.sparkzxl.mybatis.mybatis.method;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * description: 物理删除
 *
 * @author zhouxinlei
 * @since 2025-01-06 17:33:45
 */
public class DeletePhysicalById extends AbstractMethod {

    public DeletePhysicalById() {
        super(SqlMethod.DELETE_BY_ID.getMethod());
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BY_ID;
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
                tableInfo.getKeyColumn(),
                tableInfo.getKeyProperty());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Object.class);
        return this.addDeleteMappedStatement(mapperClass, methodName, sqlSource);
    }
}
