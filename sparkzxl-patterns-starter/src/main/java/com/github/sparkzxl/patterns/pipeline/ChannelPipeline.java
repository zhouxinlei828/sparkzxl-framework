package com.github.sparkzxl.patterns.pipeline;

import com.google.common.collect.Maps;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;
import java.util.Objects;

/**
 * description: 责任链管理
 *
 * @author zhouxinlei
 * @since 2023-03-23 09:08:15
 */
public class ChannelPipeline {

    /**
     * 初始化的时候造一个head,但是没有实际的处理，只是作为责任链的开始
     */
    private final Map<String, PipelineContext> pipelineContextMap = Maps.newHashMap();

    /**
     * 责任链的调用入口
     */
    public void call(String type, Object arg) {
        PipelineContext pipelineContext = pipelineContextMap.get(type);
        pipelineContext.handler(arg);
    }

    /**
     * 往链表的末尾加一个handler
     */
    public void addLast(SupperPipeline handler) {
        String type = Objects.requireNonNull(AnnotationUtils.findAnnotation(handler.getClass(), Pipeline.class)).value();
        PipelineContext headContext = intPipeline(type);
        PipelineContext pipelineContext = headContext;
        while (pipelineContext.next != null) {
            pipelineContext = pipelineContext.next;
        }
        pipelineContext.next = new PipelineContext(handler);
        pipelineContextMap.put(type, headContext);
    }


    private PipelineContext intPipeline(String type) {
        PipelineContext pipelineContext = pipelineContextMap.get(type);
        if (pipelineContext == null) {
            pipelineContext = new PipelineContext(PipelineContext::runNext);
            pipelineContextMap.put(type, pipelineContext);
        }
        return pipelineContext;
    }
}
