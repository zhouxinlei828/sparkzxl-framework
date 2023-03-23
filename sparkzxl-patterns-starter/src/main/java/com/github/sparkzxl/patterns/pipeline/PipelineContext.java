package com.github.sparkzxl.patterns.pipeline;

/**
 * description: handler上下文，我主要负责维护链，和链的执行
 *
 * @author zhouxinlei
 * @since 2023-03-23 09:03:04
 */
public class PipelineContext {

    /**
     * 下一个节点
     */
    PipelineContext next;

    /**
     * 处理管道
     */
    SupperPipeline pipeline;

    public PipelineContext(SupperPipeline pipeline) {
        this.pipeline = pipeline;

    }

    /**
     * 处理逻辑执行
     *
     * @param arg0 参数
     */
    void handler(Object arg0) {
        this.pipeline.doHandler(this, arg0);
    }

    /**
     * 继续执行下一个
     *
     * @param arg0 参数
     */
    void runNext(Object arg0) {
        if (this.next != null) {
            this.next.handler(arg0);
        }
    }
}
