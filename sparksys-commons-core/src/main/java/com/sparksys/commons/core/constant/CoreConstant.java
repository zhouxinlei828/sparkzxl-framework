package com.sparksys.commons.core.constant;

/**
 * description: 通用核心常量
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:47:09
 */
public class CoreConstant {

    /**
     * description: jwt常量
     *
     * @author zhouxinlei
     * @date 2020-05-24 12:47:17
     */
    public static final class JwtTokenConstant {

        /**
         * JWT存储的请求头
         */
        public static final String JWT_TOKEN_HEADER = "Authorization";
        /**
         * JWT加解密使用的密钥
         */
        public static final String JWT_SECRET = "secret";
        /**
         * JWT的超期限时间(60*60*24)
         */
        public static final Long JWT_EXPIRATION = 86400L;

        /**
         * JWT负载中拿到开头
         */
        public static final String JWT_TOKEN_HEAD = "Bearer ";

        public static final String CLAIM_KEY_USERNAME = "sub";

        public static final String CLAIM_KEY_CREATED = "created";
    }
}
