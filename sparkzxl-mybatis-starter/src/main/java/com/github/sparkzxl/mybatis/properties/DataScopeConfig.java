package com.github.sparkzxl.mybatis.properties;

import com.github.sparkzxl.mybatis.constant.SqlConditions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.List;

/**
 * description: 多列数据权限配置
 *
 * @author zhouxinlei
 * @since 2022-12-08 09:11:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeConfig {

    /**
     * 数据权限列
     */
    List<DataColumn> columns;
    /**
     * 数据权限id标识
     */
    private String scopeId;
    /**
     * 表名
     */
    private String tableName;
    /**
     * SQL类型
     */
    private SqlCommandType sqlCommandType;

    /**
     * 数据权限列
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataColumn {

        /**
         * 数据权限字段
         */
        private String column;
        /**
         * 条件类型
         */
        private SqlConditions condition;
        /**
         * 是否强制拼接条件
         */
        private boolean force;
        /**
         * 查询key值
         */
        private String loadKey;
    }


}
