package com.github.sparkzxl.log.queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * description: 告警任务消息队列
 *
 * @author zhouxinlei
 * @since 2022-12-26 11:17:14
 */
public class AlarmTaskQueue {

    private static final Queue<AlarmTaskInfo> QUEUE = new ConcurrentLinkedQueue<>();

    /**
     * 单例队列
     *
     * @return AlarmTaskQueue
     */
    public static AlarmTaskQueue getQueue() {
        return SingletonHolder.SINGLETON;
    }

    public void produce(AlarmTaskInfo message) {
        QUEUE.add(message);

    }

    /**
     * 延迟消费队列，取不到的话会阻塞一直到队列有消息再被唤醒。之后再取消息
     *
     * @return AlarmTaskInfo
     */
    public AlarmTaskInfo consume() {
        return QUEUE.poll();
    }

    private static class SingletonHolder {

        private static final AlarmTaskQueue SINGLETON = new AlarmTaskQueue();
    }

}
