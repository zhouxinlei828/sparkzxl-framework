package com.github.sparkzxl.core.context;

import cn.hutool.core.convert.Convert;
import com.github.sparkzxl.core.utils.StrPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 获取当前域中的 用户id appid 用户昵称
 * 注意： appid 通过token解析，  用户id 和 用户昵称必须在前端 通过请求头的方法传入。 否则这里无法获取
 *
 * @author: zhouxinlei
 * @date: '2020-12-12 10:17:51'
 */
public class BaseContextHandler {

    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? StrPool.EMPTY : value.toString());
    }

    public static <T> T get(String key, Class<T> type) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.get(key));
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.getOrDefault(key, String.valueOf(def == null ? StrPool.EMPTY : def)));
    }

    public static String get(String key) {
        Map<String, String> map = getLocalMap();
        return map.getOrDefault(key, StrPool.EMPTY);
    }

    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, String> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
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
     * @return
     */
    public static String getToken() {
        return get(BaseContextConstants.JWT_TOKEN_HEADER, String.class);
    }

    public static void setToken(String token) {
        set(BaseContextConstants.JWT_TOKEN_HEADER, token);
    }

    public static String getTenant() {
        return get(BaseContextConstants.JWT_KEY_TENANT, String.class, StrPool.EMPTY);
    }

    public static String getClientId() {
        return get(BaseContextConstants.JWT_KEY_CLIENT_ID, String.class);
    }

    public static void setTenant(String val) {
        set(BaseContextConstants.JWT_KEY_TENANT, val);
    }

    public static void setClientId(String val) {
        set(BaseContextConstants.JWT_KEY_CLIENT_ID, val);
    }


    public static Boolean getBoot() {
        return get(BaseContextConstants.IS_BOOT, Boolean.class, false);
    }

    /**
     * 账号id
     *
     * @param val 是否boot
     */
    public static void setBoot(Boolean val) {
        set(BaseContextConstants.IS_BOOT, val);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
