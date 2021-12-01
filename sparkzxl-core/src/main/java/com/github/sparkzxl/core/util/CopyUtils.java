package com.github.sparkzxl.core.util;

import com.google.common.collect.Lists;

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
public class CopyUtils {

    public static <T extends Object> List<T> deepCopy(List<T> srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            return (List<T>) inStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

    public static <T extends Object> T deepCopy(T data) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(data);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            return (T) inStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
