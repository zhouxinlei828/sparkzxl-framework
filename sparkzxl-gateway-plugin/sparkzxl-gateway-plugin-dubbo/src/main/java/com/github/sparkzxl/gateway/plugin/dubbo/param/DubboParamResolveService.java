package com.github.sparkzxl.gateway.plugin.dubbo.param;

import org.apache.commons.lang3.tuple.Pair;

/**
 * description: The interface Generic param service.
 * This service is used to construct the parameters required for the dubbo generalization.
 *
 * @author zhouxinlei
 * @since 2022-08-13 12:38:17
 */
public interface DubboParamResolveService {

    /**
     * Build parameter pair.
     * this is Resolve http body to get dubbo param.
     *
     * @param body           the body
     * @param parameterTypes the parameter types
     * @return the pair
     */
    Pair<String[], Object[]> buildParameter(String body, String parameterTypes);
}
