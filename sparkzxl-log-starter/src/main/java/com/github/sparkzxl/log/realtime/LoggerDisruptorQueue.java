package com.github.sparkzxl.log.realtime;

import com.github.sparkzxl.log.entity.LoggerMessage;
import com.github.sparkzxl.log.realtime.disruptor.*;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * description: Disruptor 环形队列
 *
 * @author zhouxinlei
 * @date 2021-06-04 10:08:47
 */
public class LoggerDisruptorQueue {

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,
            3,
            0,
            NANOSECONDS,
            new LinkedBlockingDeque<>(1000),
            new CustomizableThreadFactory());

    /**
     * The factory for the event
     */
    private LoggerEventFactory factory = new LoggerEventFactory();

    private FileLoggerEventFactory fileLoggerEventFactory = new FileLoggerEventFactory();

    /**
     * Specify the size of the ring buffer, must be power of 2.
     */
    private final int bufferSize = 2 * 1024;

    private Disruptor<LoggerEvent> disruptor = new Disruptor<>(factory, bufferSize, threadPoolExecutor);

    private Disruptor<FileLoggerEvent> fileLoggerEventDisruptor = new Disruptor<>(fileLoggerEventFactory, bufferSize, threadPoolExecutor);

    private static RingBuffer<LoggerEvent> ringBuffer;

    private static RingBuffer<FileLoggerEvent> fileLoggerEventRingBuffer;

    public LoggerDisruptorQueue(LoggerEventHandler eventHandler, FileLoggerEventHandler fileLoggerEventHandler) {
        disruptor.handleEventsWith(eventHandler);
        fileLoggerEventDisruptor.handleEventsWith(fileLoggerEventHandler);
        this.ringBuffer = disruptor.getRingBuffer();
        this.fileLoggerEventRingBuffer = fileLoggerEventDisruptor.getRingBuffer();
        disruptor.start();
        fileLoggerEventDisruptor.start();
    }

    public static void publishEvent(LoggerMessage log) {
        // Grab the next sequence
        long sequence = ringBuffer.next();
        try {
            // Get the entry in the Disruptor
            LoggerEvent event = ringBuffer.get(sequence);
            // for the sequence
            // Fill with data
            event.setLog(log);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public static void publishEvent(String log) {
        if (fileLoggerEventRingBuffer == null) {
            return;
        }
        // Grab the next sequence
        long sequence = fileLoggerEventRingBuffer.next();
        try {
            // Get the entry in the Disruptor
            FileLoggerEvent event = fileLoggerEventRingBuffer.get(sequence);
            // for the sequence
            // Fill with data
            event.setLog(log);
        } finally {
            fileLoggerEventRingBuffer.publish(sequence);
        }
    }
}
