package com.github.sparkzxl.core.json;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.core.json.impl.fastjson.FastJsonImpl;
import com.github.sparkzxl.core.json.impl.gson.GsonImpl;
import com.github.sparkzxl.core.json.impl.jackson.JacksonImpl;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * description: Json 工具类
 *
 * @author zhouxinlei
 * @since 2023-01-01 11:49:09
 */
public class JsonUtils {
    static String PREFER_JSON_FRAMEWORK_NAME = "spring.json-framework.prefer";

    private static volatile JSON json;

    public static JSON getJson() {
        if (json == null) {
            synchronized (JsonUtils.class) {
                if (json == null) {
                    String preferJsonFrameworkName = System.getProperty(PREFER_JSON_FRAMEWORK_NAME);
                    if (StringUtils.isNotEmpty(preferJsonFrameworkName)) {
                        try {
                            JSON instance = null;
                            switch (preferJsonFrameworkName) {
                                case "fastjson":
                                    instance = new FastJsonImpl();
                                    break;
                                case "jackson":
                                    instance = new JacksonImpl();
                                    break;
                                case "gson":
                                    instance = new GsonImpl();
                                    break;
                                default:
                                    break;
                            }
                            if (instance != null && instance.isSupport()) {
                                json = instance;
                            }
                        } catch (Throwable ignore) {
                            ignore.printStackTrace();
                        }
                    }
                    if (json == null) {
                        List<Class<? extends JSON>> jsonClasses = Arrays.asList(
                                JacksonImpl.class,
                                FastJsonImpl.class,
                                GsonImpl.class);
                        for (Class<? extends JSON> jsonClass : jsonClasses) {
                            try {
                                JSON instance = jsonClass.getConstructor().newInstance();
                                if (instance.isSupport()) {
                                    json = instance;
                                    break;
                                }
                            } catch (Throwable ignore) {

                            }
                        }
                    }
                    if (json == null) {
                        throw new IllegalStateException("Dubbo unable to find out any json framework (e.g. fastjson, gson) from jvm env. " +
                                "Please import at least one json framework.");
                    }
                }
            }
        }
        return json;
    }

    public static void main(String[] args) {
        System.setProperty(PREFER_JSON_FRAMEWORK_NAME, "fastjson");
        AuthUserInfo<Object> userInfo = new AuthUserInfo<>();
        userInfo.setId(IdUtil.fastSimpleUUID());
        userInfo.setAccount("zhangsan");
        userInfo.setName("张三");
        userInfo.setStatus(true);
        userInfo.setAuthorityList(Lists.newArrayList("admin"));
        JSON jsonInstance = JsonUtils.getJson();
        System.out.println("json type: " + jsonInstance.named());
        String json = jsonInstance.toJson(userInfo);
        System.out.println("json:" + json);
        String jsonPretty = jsonInstance.toJsonPretty(userInfo);
        System.out.println("jsonPretty:" + jsonPretty);
        AuthUserInfo javaObject = jsonInstance.toJavaObject(json, AuthUserInfo.class);
        System.out.println("userinfo id :" + javaObject.getId());
        AuthUserInfo<Object> authUserInfo = jsonInstance.toJavaObject(json, new TypeReference<AuthUserInfo<Object>>() {});
        System.out.println("userinfo id :" + authUserInfo.getId());
        AuthUserInfo object = jsonInstance.toJavaObject(authUserInfo, AuthUserInfo.class);
        System.out.println("userinfo id :" + object.getId());
        List<AuthUserInfo<Object>> userInfos = Lists.newArrayList(userInfo);
        String jsonList = jsonInstance.toJson(userInfos);
        System.out.println("jsonList:" + jsonList);
        List<AuthUserInfo> authUserInfos = jsonInstance.toJavaList(jsonList, AuthUserInfo.class);
        System.out.println("authUserInfos:" + authUserInfos);
        Map<String, Object> objectMap = jsonInstance.toMap(json);
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            System.out.println("key:[" + entry.getKey() + "], value:" + entry.getValue());
        }
        Map<String, Object> objectMap1 = jsonInstance.toMap(userInfo);
        for (Map.Entry<String, Object> entry : objectMap1.entrySet()) {
            System.out.println("key:[" + entry.getKey() + "], value:" + entry.getValue());
        }
        Map<String, Object> objectMap2 = jsonInstance.toMap(userInfo, Object.class);
        for (Map.Entry<String, Object> entry : objectMap2.entrySet()) {
            System.out.println("key:[" + entry.getKey() + "], value:" + entry.getValue());
        }

        Map<String, Object> objectMap3 = jsonInstance.toMap(json, Object.class);
        for (Map.Entry<String, Object> entry : objectMap3.entrySet()) {
            System.out.println("key:[" + entry.getKey() + "], value:" + entry.getValue());
        }

    }

}
