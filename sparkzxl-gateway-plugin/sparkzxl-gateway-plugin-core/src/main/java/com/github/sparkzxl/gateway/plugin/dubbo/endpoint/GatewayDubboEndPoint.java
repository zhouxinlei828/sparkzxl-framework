package com.github.sparkzxl.gateway.plugin.dubbo.endpoint;

import com.github.sparkzxl.gateway.plugin.dubbo.ApacheDubboProxyService;
import com.github.sparkzxl.gateway.plugin.dubbo.entity.DubbboRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * description: gateway dubbo endpoint
 *
 * @author zhouxinlei
 * @since 2023-01-11 16:37:57
 */
@RestController
public class GatewayDubboEndPoint {

    private final ApacheDubboProxyService dubboProxyService;

    public GatewayDubboEndPoint(ApacheDubboProxyService dubboProxyService) {
        this.dubboProxyService = dubboProxyService;
    }

    @PostMapping("/dubbo/request")
    public Mono<Object> genericInvoker(@RequestBody DubbboRequest dubbboRequest) {
        return dubboProxyService.genericInvoker(dubbboRequest);
    }

}
