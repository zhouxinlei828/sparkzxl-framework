package com.github.sparkzxl.data.sync.admin.handler;

import com.github.sparkzxl.data.sync.common.entity.PushData;

import java.util.Map;

/**
 * description: 数据合并处理
 *
 * @author zhouxinlei
 * @since 2022-09-05 14:28:18
 */
public interface MergeDataHandler<T> {

    /**
     * 合并数据处理
     *
     * @param pushData 推送数据
     * @return Map<String, T>
     */
    Map<String, T> handle(PushData<T> pushData);

    /**
     * 配置类型
     *
     * @return String
     */
    String configGroup();
}
