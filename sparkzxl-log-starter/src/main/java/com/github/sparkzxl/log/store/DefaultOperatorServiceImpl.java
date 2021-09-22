package com.github.sparkzxl.log.store;

import com.github.sparkzxl.core.context.AppContextHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 获取操作人 服务实现类
 *
 * @author zhouxinlei
 * @date 2021-09-22 11:10:13
 */
@Slf4j
public class DefaultOperatorServiceImpl implements IOperatorService {

    @Override
    public String getUserId() {
        String userId = AppContextHolder.getUserId(String.class);
        log.info("获取操作人唯一标识:{}", userId);
        return userId;
    }

    @Override
    public String getUserName() {
        String name = AppContextHolder.getName();
        log.info("获取操作人姓名:{}", name);
        return name;
    }
}
