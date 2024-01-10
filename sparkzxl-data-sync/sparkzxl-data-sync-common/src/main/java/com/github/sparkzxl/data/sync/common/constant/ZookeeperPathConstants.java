package com.github.sparkzxl.data.sync.common.constant;


import com.github.sparkzxl.core.util.StrPool;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-09-08 09:20:20
 */
public class ZookeeperPathConstants {

    public static String buildPath(String group, String path) {
        if (!group.startsWith(StrPool.SLASH)) {
            return StrPool.SLASH.concat(String.join(StrPool.SLASH, group, path));
        }
        return String.join(StrPool.SLASH, group, path);
    }
}
