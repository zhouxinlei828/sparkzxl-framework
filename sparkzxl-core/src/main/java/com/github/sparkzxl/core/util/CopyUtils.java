package com.github.sparkzxl.core.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * description: 克隆工具类
 *
 * @author zhouxinlei
 */
@Slf4j
public class CopyUtils {

    public static <T extends Object> List<T> deepCopy(List<T> dataList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(dataList);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            return (List<T>) inStream.readObject();
        } catch (Exception e) {
            log.error("deep copy data list exception message:{}", e.getMessage());
            return Lists.newArrayList();
        }
    }

    public static <T> T deepCopy(T data) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(data);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            return (T) inStream.readObject();
        } catch (Exception e) {
            log.error("deep copy data exception message:{}", e.getMessage());
            return null;
        }
    }
}
