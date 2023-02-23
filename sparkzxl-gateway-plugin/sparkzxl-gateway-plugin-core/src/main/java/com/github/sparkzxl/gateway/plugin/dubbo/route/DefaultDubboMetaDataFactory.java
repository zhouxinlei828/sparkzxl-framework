package com.github.sparkzxl.gateway.plugin.dubbo.route;

import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.core.util.ListUtils;
import com.github.sparkzxl.gateway.common.constant.enums.RpcTypeEnum;
import com.github.sparkzxl.gateway.common.entity.MetaData;
import com.github.sparkzxl.gateway.plugin.dubbo.constant.DubboConstant;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.cloud.gateway.route.Route;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

/**
 * description: 用于构建Dubbo 元数据
 *
 * @author zhouxinlei
 * @since 2022-08-12 15:33:32
 */
@SuppressWarnings(value = "unchecked")
public class DefaultDubboMetaDataFactory implements DubboMetaDataFactory {

    /**
     * Dubbo路由缓存
     */
    private final Table<String, Integer, MetaData> dubboMetaDataTable = HashBasedTable.create();

    @Override
    public MetaData get(Route route) {
        Map<Integer, MetaData> metaDataMap = dubboMetaDataTable.row(route.getId());
        MetaData existsMetaData = metaDataMap.entrySet().stream().filter(entry -> entry.getKey().equals(route.hashCode()))
                .map(Map.Entry::getValue).findAny().orElse(null);
        if (ObjectUtils.isNotEmpty(existsMetaData)) {
            return existsMetaData;
        }
        //删除冗余缓存
        if (CollectionUtils.isNotEmptyMap(metaDataMap)) {
            metaDataMap.keySet().forEach(hashCode -> dubboMetaDataTable.remove(route.getId(), hashCode));
        }
        MetaData metaData = this.build(route);
        assert metaData != null;
        dubboMetaDataTable.put(route.getId(), route.hashCode(), metaData);
        return metaData;
    }

    /**
     * 通过URI构建调用Meta对象
     * dubbo://service-name/interface/method
     *
     * @param route 网关路由
     * @return MetaData
     */
    private MetaData build(Route route) {
        URI uri = route.getUri();
        String serviceName = uri.getHost();
        String path = uri.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] parts = path.split("/");
        String interfaceClass = parts[0];
        String method = parts[1];
        Object dubboMetadata = route.getMetadata().get(DubboConstant.DUBBO);
        if (dubboMetadata == null) {
            return null;
        }
        Map<String, Object> metadataMap = (Map<String, Object>) dubboMetadata;
        String[] parameterTypes = new String[]{};
        Object parameterTypesMetadata = metadataMap.get(DubboConstant.PARAMETER_TYPES);
        if (parameterTypesMetadata != null) {
            Collection<String> parameterTypeList = ((Map<String, String>) parameterTypesMetadata).values();
            parameterTypes = parameterTypeList.toArray(new String[]{});
        }
        MetaData metaData = new MetaData();
        metaData.setId(IdUtil.fastSimpleUUID());
        metaData.setEnabled(Boolean.TRUE);
        metaData.setAppName(serviceName);
        metaData.setRpcType(RpcTypeEnum.DUBBO.getName());
        String namespace = (String) metadataMap.getOrDefault(DubboConstant.NAMESPACE, DubboConstant.DEFAULT_NAMESPACE);
        metaData.setNamespace(namespace);
        metaData.setServiceName(interfaceClass);
        metaData.setMethodName(method);
        String parameterTypeStr = ListUtils.arrayToString(parameterTypes);
        metaData.setParameterTypes(parameterTypeStr);
        Object rpcExt = metadataMap.get(DubboConstant.RPC_EXT);
        if (ObjectUtils.isNotEmpty(rpcExt)) {
            metaData.setRpcExt(String.valueOf(rpcExt));
        }
        return metaData;
    }
}
