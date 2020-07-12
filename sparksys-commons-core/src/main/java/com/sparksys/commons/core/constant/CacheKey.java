package com.sparksys.commons.core.constant;

import cn.hutool.core.util.StrUtil;

public class CacheKey {

    /**
     * 登录账户 前缀
     * 完整key: authUser:{key} -> str
     */
    public static String AUTH_USER = "login_user";

    /**
     * 验证码 前缀
     * 完整key: captcha:{key} -> str
     */
    public static String CAPTCHA = "captcha";
    /**
     * token 前缀
     * 完整key： token:{token} -> userid
     */
    public static String TOKEN = "token";


    /**
     * 菜单 前缀
     * 完整key: menu:{menuId} -> obj
     */
    public static String MENU = "menu";
    /**
     * 组织 前缀
     * 完整key: station:{stationId} -> obj
     */
    public static String ORG = "org";
    /**
     * 岗位 前缀
     * 完整key: station:{stationId} -> obj
     */
    public static String STATION = "station";

    /**
     * 资源 前缀
     * 完整key: resource:{resourceId} -> obj
     */
    public static String RESOURCE = "resource";

    /**
     * 角色 前缀
     * 完整key: role:{roleId}
     */
    public static String ROLE = "role";

    /**
     * 角色拥有那些菜单 前缀
     * 完整key: role_menu:{ROLE_ID} -> [MENU_ID, MENU_ID, ...]
     */
    public static String ROLE_MENU = "role_menu";
    /**
     * 角色拥有那些资源 前缀
     * 完整key: role_resource:{ROLE_ID} -> [RESOURCE_ID, ...]
     */
    public static String ROLE_RESOURCE = "role_resource";
    /**
     * 角色拥有那些组织 前缀
     * 完整key: role_org:{ROLE_ID} -> [ORG_ID, ...]
     */
    public static String ROLE_ORG = "role_org";

    /**
     * 用户 前缀
     * 完整key: user:classTypeName:{USER_ID} -> [ROLE_ID, ...]
     */
    public static String USER = "user";

    /**
     * 用户拥有那些角色 前缀
     * 完整key: user_role:{USER_ID} -> [ROLE_ID, ...]
     */
    public static String USER_ROLE = "user_role";
    /**
     * 用户拥有的菜单 前缀
     * 完整key: user_menu:{userId} -> [MENU_ID, MENU_ID, ...]
     */
    public static String USER_MENU = "user_menu";
    /**
     * 用户拥有的资源 前缀
     * 完整key: user_resource:{userId} -> [RESOURCE_ID, ...]
     */
    public static String USER_RESOURCE = "user_resource";

    /**
     * 登录总次数
     * login_log_total:{TENANT} -> Long
     */
    public static String LOGIN_LOG_TOTAL = "login_log_total";
    /**
     * 今日登录总次数
     * login_log_today:{TENANT}:{today} -> Long
     */
    public static String LOGIN_LOG_TODAY = "login_log_today";
    /**
     * 今日登录总ip
     * login_log_todayip:{TENANT}:{today} -> Map
     */
    public static String LOGIN_LOG_TODAY_IP = "login_log_todayip";
    /**
     * 最近10访问记录
     * login_log_tenday:{TENANT}:{today}:{account} -> Map
     */
    public static String LOGIN_LOG_TEN_DAY = "login_log_tenday";
    /**
     * 登录总次数
     * login_log_browser:{TENANT} -> Map
     */
    public static String LOGIN_LOG_BROWSER = "login_log_browser";
    /**
     * 登录总次数
     * login_log_system{TENANT} -> Map
     */
    public static String LOGIN_LOG_SYSTEM = "login_log_system";

    /**
     * 地区 前缀
     * 完整key: area:{id} -> obj
     */
    public static String AREA = "area";
    /**
     * 所有地区 前缀
     * 完整key: area_all -> [AREA_ID]
     */
    public static String AREA_ALL = "area_all";

    /**
     * 字典 前缀
     * 完整key: dictionary_item:{id} -> obj
     */
    public static String DICTIONARY_ITEM = "dictionary_item";

    /**
     * 参数 前缀
     * 完整key: parameter:{id} -> obj
     */
    public static String PARAMETER = "parameter";
    /**
     * 用户登录的客户端 前缀： 用于记录用户在那几个设备上登录了
     * 完整key: user_login_client:{userid} -> [client, client, ...] (Set)
     */
    public static String USER_LOGIN_CLIENT = "user_login_client";

    /**
     * 用户客户端token 前缀
     * 完整key: user_client_token:{userid}:{client} -> token (String)
     */
    public static String USER_CLIENT_TOKEN = "user_client_token";

    /**
     * 用户token 前缀
     * 完整key: user_token:{userid} -> token (String)
     */
    public static String USER_TOKEN = "user_token";

    /**
     * 用户token 前缀
     * 完整key: token_user_id:{token} -> userid (Long)
     */
    public static String TOKEN_USER_ID = "token_user_id";

    // 消息服务缓存 start
    /**
     * 用户注册 前缀
     * 完整key: register:{注册类型}:{手机号}
     */
    public static String REGISTER_USER = "register";
    // 消息服务缓存 end

    /**
     * 订单服务 前缀
     * 完整key: register:{注册类型}:{手机号}
     */
    public static String OMS_ORDER = "oms_order";

    /**
     * 构建没有租户信息的key
     *
     * @param args
     * @return
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
        System.out.println(buildKey(CacheKey.TOKEN, null));
        System.out.println(key("111", 222, 333));
        System.out.println(CacheKey.buildKey(CacheKey.AUTH_USER, "11111"));
    }
}
