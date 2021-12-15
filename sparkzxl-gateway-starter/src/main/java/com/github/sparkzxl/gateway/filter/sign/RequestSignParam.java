package com.github.sparkzxl.gateway.filter.sign;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;

/**
 * description: 请求签名实体对象
 *
 * @author zhouxinlei
 * @date 2021-12-15 20:28
 */
@Data
@Builder
public class RequestSignParam {

    private String path;

    private HttpMethod method;

    private Long timestamp;

    private String nonce;

    private String signature;

    private String accessToken;

}
