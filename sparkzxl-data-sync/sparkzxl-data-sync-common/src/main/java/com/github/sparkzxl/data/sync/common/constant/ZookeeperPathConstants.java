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
        if (!group.startsWith(StrPool.PATH_SEPARATOR)) {
            return StrPool.PATH_SEPARATOR.concat(String.join(StrPool.PATH_SEPARATOR, group, path));
        }
        return String.join(StrPool.PATH_SEPARATOR, group, path);
    }
}
