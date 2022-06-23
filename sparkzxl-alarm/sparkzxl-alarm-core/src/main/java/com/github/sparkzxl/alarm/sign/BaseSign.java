package com.github.sparkzxl.alarm.sign;

/**
 * description: 签名返回体基础类
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:53:32
 */
public abstract class BaseSign {

    protected final static String SEPERATOR = "&";

    /**
     * 签名对象转字符串
     *
     * @return 返回转换后结果
     */
    public abstract String transfer();

}
