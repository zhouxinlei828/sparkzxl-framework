package com.github.sparkzxl.log.store;

import com.github.sparkzxl.log.entity.OptLogRecordDetail;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 日志持久化默认实现
 *
 * @author zhouxinlei
 * @date 2021-09-22 10:51:22
 */
@Slf4j
public class DefaultLogRecordServiceImpl implements ILogRecordService {

    @Override
    public void record(OptLogRecordDetail optLogRecordDetail) {
        log.info("操作人【{}】：操作日志：【{}】", optLogRecordDetail.getOperator(), optLogRecordDetail.getDetail());
    }
}
