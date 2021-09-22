package com.github.sparkzxl.log.store;

import com.github.sparkzxl.log.entity.OptLogRecordDetail;

/**
 * description: 操作日志持久化
 *
 * @author zhouxinlei
 * @date 2021-09-22 10:50:38
 */
public interface ILogRecordService {

    /**
     * 保存 log
     *
     * @param optLogRecordDetail 日志实体
     */
    void record(OptLogRecordDetail optLogRecordDetail);
}
