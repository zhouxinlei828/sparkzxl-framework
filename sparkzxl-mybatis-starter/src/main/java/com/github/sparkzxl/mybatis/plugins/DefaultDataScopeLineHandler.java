package com.github.sparkzxl.mybatis.plugins;

import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.mybatis.annotation.DataScope;
import com.github.sparkzxl.mybatis.constant.SqlConditions;
import com.github.sparkzxl.mybatis.properties.DataScopeConfig;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: 多列数据权限 行级处理器
 *
 * @author zhouxinlei
 * @since 2022-11-14 11:01:53
 */
public class DefaultDataScopeLineHandler implements DataScopeLineHandler {

    private final Map<String, DataScopeConfig> dataScopeMap;
    private final ThreadLocal<DataScopeConfig> dataScopeThreadLocal = new InheritableThreadLocal<>();
    private final ThreadLocal<Map<String, DataScopeConfig.DataColumn>> columnThreadLocal = new InheritableThreadLocal<>();

    public DefaultDataScopeLineHandler(List<DataScopeConfig> dataScopeList) {
        if (CollectionUtils.isEmpty(dataScopeList)) {
            dataScopeMap = Maps.newConcurrentMap();
        } else {
            dataScopeMap = dataScopeList.stream().collect(Collectors.toMap(DataScopeConfig::getScopeId, k -> k));
        }

    }

    @Override
    public boolean match() {
        DataScope dataScope = RequestLocalContextHolder.get(BaseContextConstants.MULTI_DATA_SCOPE, DataScope.class);
        if (ObjectUtils.isEmpty(dataScope)) {
            return false;
        }
        DataScopeConfig dataScopeConfig = dataScopeMap.get(dataScope.value());
        ArgumentAssert.notNull(dataScopeConfig, "未找到[{}]数据权限配置，请联系管理员！", dataScope.value());
        dataScopeThreadLocal.set(dataScopeConfig);
        Map<String, DataScopeConfig.DataColumn> dataColumnMap = dataScopeConfig.getColumns().stream().collect(
                Collectors.toMap(DataScopeConfig.DataColumn::getColumn, k -> k));
        columnThreadLocal.set(dataColumnMap);
        return true;
    }

    @Override
    public SqlConditions getSqlCondition(String columnName) {
        return columnThreadLocal.get().get(columnName).getCondition();
    }

    @Override
    public List<String> getScopeIdColumnList() {
        return new ArrayList<>(columnThreadLocal.get().keySet());
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return !tableName.equalsIgnoreCase(dataScopeThreadLocal.get().getTableName());
    }

    @Override
    public String getScopeId(String columnName) {
        DataScopeConfig.DataColumn dataColumn = columnThreadLocal.get().get(columnName);
        String val = RequestLocalContextHolder.get(dataColumn.getLoadKey());
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        return val;
    }

    @Override
    public List<String> getScopeIdList(String columnName) {
        DataScopeConfig.DataColumn dataColumn = columnThreadLocal.get().get(columnName);
        List<String> dataPermissions = RequestLocalContextHolder.getList(dataColumn.getLoadKey());
        if (CollectionUtils.isEmpty(dataPermissions)) {
            return null;
        }
        return dataPermissions;
    }

    @Override
    public void remove() {
        dataScopeThreadLocal.remove();
        columnThreadLocal.remove();
    }
}
