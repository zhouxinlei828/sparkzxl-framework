package com.github.sparkzxl.core.context;

import cn.hutool.core.convert.Convert;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.core.utils.StrPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 获取当前域中的 用户id appid 用户昵称
 * 注意： appid 通过token解析，  用户id 和 用户昵称必须在前端 通过请求头的方法传入。 否则这里无法获取
 *
 * @author zhouxinlei
 */
public class BaseContextHandler {

    public static void set(String key, Object value) {
        RequestContextHolderUtils.setAttribute(key,value == null ? StrPool.EMPTY : value.toString());
    }

    public static <T> T get(String key, Class<T> type) {
        Object attribute = RequestContextHolderUtils.getAttribute(key);
        return Convert.convert(type, attribute);
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Object attribute = RequestContextHolderUtils.getAttribute(key);
        return Convert.convert(type, attribute == null ? def : attribute);
    }

    public static String get(String key) {
        Object attribute = RequestContextHolderUtils.getAttribute(key);
        return attribute == null ? "" : (String) attribute;
    }


    public static <T> T getUserId(Class<T> tClass) {
        return get(BaseContextConstants.JWT_KEY_USER_ID, tClass, 0L);
    }

    public static <T> void setUserId(T userId) {
        set(BaseContextConstants.JWT_KEY_USER_ID, userId);
    }

    /**
     * 账号表中的name
     *
     * @return String
     */
    public static String getAccount() {
        return get(BaseContextConstants.JWT_KEY_ACCOUNT, String.class);
    }

    /**
     * 账号表中的name
     *
     * @param account 账户
     */
    public static void setAccount(String account) {
        set(BaseContextConstants.JWT_KEY_ACCOUNT, account);
    }


    /**
     * 获取登录的用户姓名
     *
     * @return String
     */
    public static String getName() {
        return get(BaseContextConstants.JWT_KEY_NAME, String.class);
    }

    /**
     * 登录的账号
     *
     * @param name 用户姓名
     */
    public static void setName(String name) {
        set(BaseContextConstants.JWT_KEY_NAME, name);
    }

    /**
     * 获取用户token
     *
     * @return String
     */
    public static String getToken() {
        return get(BaseContextConstants.JWT_TOKEN_HEADER, String.class);
    }

    public static void setToken(String token) {
        set(BaseContextConstants.JWT_TOKEN_HEADER, token);
    }

    public static String getRealm() {
        return get(BaseContextConstants.JWT_KEY_REALM, String.class, StrPool.EMPTY);
    }

    public static String getClientId() {
        return get(BaseContextConstants.JWT_KEY_CLIENT_ID, String.class);
    }

    public static void setRealm(String val) {
        set(BaseContextConstants.JWT_KEY_REALM, val);
    }

    public static void setClientId(String val) {
        set(BaseContextConstants.JWT_KEY_CLIENT_ID, val);
    }

    public static Boolean getBoot() {
        return get(BaseContextConstants.IS_BOOT, Boolean.class, false);
    }

    public static void setBoot(Boolean val) {
        set(BaseContextConstants.IS_BOOT, val);
    }
}
