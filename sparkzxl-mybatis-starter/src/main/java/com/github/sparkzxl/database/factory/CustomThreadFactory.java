package com.github.sparkzxl.database.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: 自定义ThreadFactory
 *
 * @author zhouxinlei
 */
public class CustomThreadFactory implements ThreadFactory {

    private final AtomicInteger count = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        String threadName = CustomThreadFactory.class.getSimpleName() + count.addAndGet(1);
        System.out.println(threadName);
        t.setName(threadName);
        return t;
    }
}
