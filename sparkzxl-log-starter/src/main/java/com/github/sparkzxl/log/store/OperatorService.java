package com.github.sparkzxl.log.store;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 获取操作人 服务实现类
 *
 * @author zhouxinlei
 */
@Slf4j
public class OperatorService {

    public String getUserId() {
        return RequestLocalContextHolder.getUserId(String.class);
    }

    public String getUserName() {
        return RequestLocalContextHolder.getName();
    }
}
