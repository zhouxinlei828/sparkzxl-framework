package com.github.sparkzxl.mybatis.plugins;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.database.DataScope;
import com.github.sparkzxl.mybatis.properties.DataProperties;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: 全局数据权限处理器
 *
 * @author zhouxinlei
 * @since 2022-07-2022/7/18 16:35:40}
 */
@Getter
@Setter
@Component
public class DefaultDataScopeLineHandler implements DataScopeLineHandler {

    private final Map<String, DataProperties.DataScope> dataScopeMap;
    private DataProperties.DataScope current;

    public DefaultDataScopeLineHandler(List<DataProperties.DataScope> dataScopeList) {
        if (CollectionUtils.isEmpty(dataScopeList)) {
            dataScopeMap = Maps.newConcurrentMap();
        } else {
            this.dataScopeMap = dataScopeList.stream().collect(Collectors.toMap(DataProperties.DataScope::getScopeId, k -> k));
        }
    }

    @Override
    public boolean match() {
        DataScope dataScope = RequestLocalContextHolder.get(BaseContextConstants.DATA_SCOPE, DataScope.class);
        if (ObjectUtils.isEmpty(dataScope)) {
            return false;
        }
        this.current = dataScopeMap.get(dataScope.value());
        return true;
    }

    @Override
    public Expression getScopeId() {
        String val = RequestLocalContextHolder.get(current.getLoadKey());
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        return new StringValue(val);
    }

    @Override
    public String getScopeIdColumn() {
        return current.getColumn();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 如果等于，则需要解析并拼接scope条件
        return !tableName.equalsIgnoreCase(current.getTableName());
    }

    @Override
    public ValueListExpression getScopeIdList() {
        ValueListExpression valueListExpression = new ValueListExpression();
        List<String> dataPermissions = RequestLocalContextHolder.getList(current.getLoadKey(), String.class);
        if (CollectionUtils.isEmpty(dataPermissions)) {
            return null;
        }
        List<Expression> expressionList = dataPermissions.stream().map(StringValue::new).collect(Collectors.toList());
        valueListExpression.setExpressionList(new ExpressionList(expressionList));
        return valueListExpression;
    }
}
