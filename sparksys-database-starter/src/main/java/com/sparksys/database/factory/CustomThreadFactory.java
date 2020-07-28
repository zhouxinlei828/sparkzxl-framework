package com.sparksys.database.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: 自定义ThreadFactory
 *
 * @author: zhouxinlei
 * @date: 2020-07-28 16:24:56
 */
public class CustomThreadFactory implements ThreadFactory {

    private final AtomicInteger count = new AtomicInteger(3);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        String threadName = CustomThreadFactory.class.getSimpleName() + count.addAndGet(1);
        System.out.println(threadName);
        t.setName(threadName);
        return t;
    }
}
