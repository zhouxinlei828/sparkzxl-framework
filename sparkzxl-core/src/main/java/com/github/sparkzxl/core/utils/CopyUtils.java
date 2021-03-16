package com.github.sparkzxl.core.utils;

import com.google.common.collect.Lists;
import io.vavr.control.Try;

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
        return Try.of(() -> {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            return (List<T>) inStream.readObject();
        }).onFailure(Throwable::printStackTrace).getOrElse(Lists.newArrayList());
    }

    public static <T extends Object> T deepCopy(T data) {
        return Try.of(() -> {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(data);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            return (T) inStream.readObject();
        }).onFailure(Throwable::printStackTrace).getOrElse((T) new Object());
    }
}
