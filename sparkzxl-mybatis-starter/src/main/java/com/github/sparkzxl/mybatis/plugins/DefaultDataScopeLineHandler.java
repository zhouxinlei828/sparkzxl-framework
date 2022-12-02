package com.github.sparkzxl.mybatis.plugins;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.database.DataScope;
import com.github.sparkzxl.mybatis.constant.SqlConditions;
import com.github.sparkzxl.mybatis.properties.DataProperties;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
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
    private ThreadLocal<DataProperties.DataScope> scopeThreadLocal = new InheritableThreadLocal<>();

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
        scopeThreadLocal.set(dataScopeMap.get(dataScope.value()));
        return true;
    }

    @Override
    public SqlCommandType getSqlCommandType() {
        return scopeThreadLocal.get().getSqlCommandType();
    }

    @Override
    public SqlConditions getSqlCondition() {
        DataProperties.DataScope dataScope = scopeThreadLocal.get();
        if (dataScope.getCondition() == null) {
            return DataScopeLineHandler.super.getSqlCondition();
        }
        return dataScope.getCondition();
    }

    @Override
    public String getScopeId() {
        String val = RequestLocalContextHolder.get(scopeThreadLocal.get().getLoadKey());
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        return val;
    }

    @Override
    public String getScopeIdColumn() {
        return scopeThreadLocal.get().getColumn();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 如果等于，则需要解析并拼接scope条件
        return !tableName.equalsIgnoreCase(scopeThreadLocal.get().getTableName());
    }

    @Override
    public List<String> getScopeIdList() {
        List<String> dataPermissions = RequestLocalContextHolder.getList(scopeThreadLocal.get().getLoadKey());
        if (CollectionUtils.isEmpty(dataPermissions)) {
            return null;
        }
        return dataPermissions;
    }

    @Override
    public void remove() {
        scopeThreadLocal.remove();
    }
}
