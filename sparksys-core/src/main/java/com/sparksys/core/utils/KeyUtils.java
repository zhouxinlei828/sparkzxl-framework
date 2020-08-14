package com.sparksys.core.utils;

import cn.hutool.core.util.StrUtil;
import com.sparksys.core.constant.BaseContextConstant;


/**
 * description: 缓存key前缀
 *
 * @author: zhouxinlei
 * @date: 2020-07-13 14:07:32
 */
public class KeyUtils {

    /**
     * 构建key
     *
     * @param args 参数
     * @return String
     */
    public static String buildKey(String template, Object... args) {
        StringBuilder key = new StringBuilder();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                template = template.concat(":{}");
            }
            key.append(StrUtil.format(template, args));
        }
        return key.toString();
    }


    public static String buildKey(Object... args) {
        if (args.length == 1) {
            return String.valueOf(args[0]);
        } else {
            return args.length > 0 ? StrUtil.join(":", args) : "";
        }
    }

    public static String key(Object... args) {
        return buildKey(args);
    }

    public static void main(String[] args) {
        String tokenKey = KeyUtils.buildKey(BaseContextConstant.AUTH_USER, "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJ1c2VyX25hbWUiOiJ6aG91eGlubGVpIiwic2NvcGUiOlsiYWxsIl0sImlkIjoxMjQ4MDg0MTA5NDUyOTAyNDAwLCJleHAiOjE1OTc0NzY2NjIsImF1dGhvcml0aWVzIjpbIlRFU1QiLCJBRE1JTiJdLCJqdGkiOiI5MzA2MGYyZS00NGY2LTQ4YWYtYjQ2ZC0zYzY5ZDliMTk3N2EiLCJjbGllbnRfaWQiOiJzcGFya3N5cyJ9.gAidBNyb4rr3kzTIdFU6XaPU99G0PuD3So5dRFtbyjRvV_Ke5x4Gk1q97GO3O3qCiWBSha-vpXJ1vAyHw-3_amKexXAlJXihKQxpSIIYpN4rG0nmB4dMZQ5lIAED0Z4lt3DlRHPmVrzTQEJm2tNDKy9pqPXFMC8krwpD1iJoOHtDo7gv-qMswZyWeL6xmqexmjLnV_ctf3dFpB4z0EPeTZxHry5e0KWzbLfjuQXufnTscjC3K6t0g-15eqr1pl7RsprkXfJvh9aWBWhKdAOZ1emVWDexUU7y-iwnU7oWqjBDM82O2Mgi-4OGNjHoIuX-8OmH9zXAQx8mlE4sjRqehA");
        System.out.println(Md5Utils.encrypt(tokenKey));
    }
}
