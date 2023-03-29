package com.github.sparkzxl.gateway.plugin.jwt;

import java.io.Serializable;
import lombok.Data;

/**
 * description: jwt配置
 *
 * @author zhouxinlei
 * @since 2022-01-08 22:32:17
 */
@Data
public class JwtConfig implements Serializable {

    private static final long serialVersionUID = -7123471949294999926L;

    private String secretKey;

    private String tokenKey;

}
