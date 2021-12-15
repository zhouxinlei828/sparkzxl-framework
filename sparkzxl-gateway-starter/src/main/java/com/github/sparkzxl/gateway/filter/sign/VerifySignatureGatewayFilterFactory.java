package com.github.sparkzxl.gateway.filter.sign;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import javax.validation.constraints.NotBlank;

/**
 * description: 验签过滤器
 * 禁止 URL 传参形式: ?name=tom,jack&name=luck (将导致验签失败);
 * ?name=&age=12   将视 name对应的值为空串进行验签
 *
 * @author zhouxinlei
 * @date 2021-12-15 20:32
 */
public class VerifySignatureGatewayFilterFactory extends AbstractGatewayFilterFactory<VerifySignatureGatewayFilterFactory.Config> {


    @Override
    public GatewayFilter apply(Config config) {
        return null;
    }

    @Data
    public static class Config {

        @NotBlank(message = "必须设置 routerId ")
        private String routerId;

        /**
         * 是否防重放
         */
        private Boolean nonceCheck = false;

    }
}
