package com.sparksys.core.constant;

/**
 * description: 通用核心常量
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:47:09
 */
public class CoreConstant {


    /**
     * 空null
     */
    public static String NULL_STRING = "null";

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
         * JWT的超期限时间(60*60*24)
         */
        public static final Long JWT_EXPIRATION = 86400L;

        /**
         * JWT负载中拿到开头
         */
        public static final String JWT_TOKEN_HEAD = "Bearer ";

    }
}
