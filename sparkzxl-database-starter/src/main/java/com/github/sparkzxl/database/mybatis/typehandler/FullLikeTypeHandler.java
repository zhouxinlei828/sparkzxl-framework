package com.github.sparkzxl.database.mybatis.typehandler;

import org.apache.ibatis.type.Alias;

/**
 * description: 仅仅用于like查询
 *
 * @author: zhouxinlei
 * @date: 2021-03-02 13:38:28
*/
@Alias("fullLike")
public class FullLikeTypeHandler extends BaseLikeTypeHandler {
    public FullLikeTypeHandler() {
        super(true, true);
    }
}
