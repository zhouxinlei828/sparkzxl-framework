package com.github.sparkzxl.mybatis.plugins;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ValueListExpression;

/**
 * description: 数据权限 行级处理器
 *
 * @author zhouxinlei
 * @since 2022-07-2022/7/18 13:43:58}
 */
public interface DataScopeLineHandler {

    /**
     * 判断是否匹配拼接权限条件
     * <p>
     * 默认都不进行解析并拼接权限条件
     *
     * @return 是否忽略, true:表示拼接权限条件，false:不需要拼接权限条件
     */
    default boolean match() {
        return true;
    }

    /**
     * 获取数据权限值表达式，只支持单个 ID 值
     *
     * @return scopeId 值表达式
     */
    Expression getScopeId();

    /**
     * 数据权限字段列
     *
     * @return String
     */
    default String getScopeIdColumn() {
        return "scope_code";
    }

    /**
     * 根据表名判断是否忽略拼接scope条件
     * <p>
     * 默认不进行解析并拼接scope条件
     *
     * @param tableName 表名
     * @return 是否忽略, true:表示忽略，false:需要解析并拼接scope条件
     */
    default boolean ignoreTable(String tableName) {
        return true;
    }


    /**
     * 获取数据权限值表达式，支持多个 ID 值
     *
     * @return 数据权限值表达式
     */
    ValueListExpression getScopeIdList();
}
