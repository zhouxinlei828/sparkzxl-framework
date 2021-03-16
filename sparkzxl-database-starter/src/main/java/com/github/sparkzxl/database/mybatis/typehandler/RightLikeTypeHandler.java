package com.github.sparkzxl.database.mybatis.typehandler;


import org.apache.ibatis.type.Alias;

/**
 * description: 仅仅用于like查询
 *
 * @author zhouxinlei
 */
@Alias("rightLike")
public class RightLikeTypeHandler extends BaseLikeTypeHandler {
    public RightLikeTypeHandler() {
        super(false, true);
    }
}

