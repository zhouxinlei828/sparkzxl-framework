package com.github.sparkzxl.mybatis.properties;

import com.github.sparkzxl.mybatis.constant.SqlConditions;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 数据权限id标识
     */
    private String scopeId;

    /**
     * 数据权限列
     */
    List<DataColumn> columns;
    /**
     * 表名
     */
    private String tableName;

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
         * 查询key值
         */
        private String loadKey;
    }


}
