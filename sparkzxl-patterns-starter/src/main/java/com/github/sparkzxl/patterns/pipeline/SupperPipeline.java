package com.github.sparkzxl.patterns.pipeline;

/**
 * description: 通用处理管道
 *
 * @author zhouxinlei
 * @since 2023-03-23 08:52:21
 */
public interface SupperPipeline {

    /**
     * 处理逻辑执行
     *
     * @param arg0 请求参数
     */
    void doHandler(PipelineContext context, Object arg0);
}
