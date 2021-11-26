package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.core.util.HttpRequestUtils;
import lombok.Data;

import java.util.*;

/**
 * description: 操作日志配置
 *
 * @author zhouxinlei
 * @date 2021-11-26 15:59:39
 */
@Data
public class OpLogRuleMatch {

    /**
     * HOST 下精确配置路径
     */
    private Map<String, Set<String>> hostExactPathSetMap = Collections.emptyMap();

    /**
     * Host 下匹配型路径
     */
    private Map<String, List<String>> hostPatternPathListMap = Collections.emptyMap();

    public boolean match(String host, String path) {
        return Optional.ofNullable(hostExactPathSetMap.get(host))
                .map(s -> s.contains(path))
                .orElseGet(() -> Optional.ofNullable(hostPatternPathListMap.get(host))
                        .map(l -> l.stream().anyMatch(s ->
                                HttpRequestUtils.isMatchPath(s, path)))
                        .orElse(false));
    }

}
