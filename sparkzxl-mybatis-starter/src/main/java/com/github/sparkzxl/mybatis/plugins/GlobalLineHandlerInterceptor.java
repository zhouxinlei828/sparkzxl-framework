package com.github.sparkzxl.mybatis.plugins;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

import java.util.List;

/**
 * description: 全局行级处理器
 *
 * @author zhouxinlei
 */
public class GlobalLineHandlerInterceptor implements TenantLineHandler {

    private final String tenantIdColumn;
    private final List<String> ignoreTableList;
    private final String loadKey;

    public GlobalLineHandlerInterceptor(String tenantIdColumn, String loadKey, List<String> ignoreTableList) {
        this.tenantIdColumn = tenantIdColumn;
        this.loadKey = loadKey;
        this.ignoreTableList = ignoreTableList;
    }

    @Override
    public Expression getTenantId() {
        return new StringValue(RequestLocalContextHolder.get(loadKey));
    }

    @Override
    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return ignoreTableList.contains(tableName);
    }
}
