package com.sparksys.commons.cloud.ribbon;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 16:16:29
 */
public class MetadataAwarePredicate extends DiscoveryEnabledPredicate {

    @Override
    protected boolean apply(NacosServer nacosServer) {
        //根据客户端传入的版本号进行过滤，此处可自行设计扩展
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String versionNo = request.getHeader("version");
        Map<String, String> versionMap = new HashMap<>(1);
        versionMap.put("version", versionNo);
        final Set<Map.Entry<String, String>> attributes =
                Collections.unmodifiableSet(versionMap.entrySet());
        final Map<String, String> metadata = nacosServer.getInstance().getMetadata();
        return metadata.entrySet().containsAll(attributes);
    }

}
