package com.github.sparkzxl.core.task.timer;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: TimingWheel .
 * <p>This is a Hierarchical wheel timer implementation.</p>
 *
 * @author zhouxinlei
 * @since 2022-08-25 13:40:08
 */
class TimingWheel {

    private final Long tickMs;

    private final Integer wheelSize;

    private final AtomicInteger taskCounter;

    private final DelayQueue<TimerTaskList> queue;

    private final Long interval;

    private final TimerTaskList[] buckets;

    private Long currentTime;

    private TimingWheel overflowWheel;

    /**
     * Instantiates a new Timing wheel.
     *
     * @param tickMs      the tick ms
     * @param wheelSize   the wheel size
     * @param startMs     the start ms
     * @param taskCounter the task counter
     * @param queue       the queue
     */
    TimingWheel(final Long tickMs, final Integer wheelSize, final Long startMs, final AtomicInteger taskCounter,
            final DelayQueue<TimerTaskList> queue) {
        this.tickMs = tickMs;
        this.wheelSize = wheelSize;
        this.taskCounter = taskCounter;
        this.queue = queue;
        this.interval = tickMs * wheelSize;
        this.currentTime = startMs - (startMs % tickMs);
        this.buckets = new TimerTaskList[wheelSize];
    }

    private synchronized void addOverflowWheel() {
        if (overflowWheel == null) {
            overflowWheel = new TimingWheel(interval, wheelSize, currentTime, taskCounter, queue);
        }
    }

    /**
     * Add boolean.
     *
     * @param taskEntry the task entry
     * @return the boolean
     */
    boolean add(final TimerTaskList.TimerTaskEntry taskEntry) {
        Long expirationMs = taskEntry.getExpirationMs();
        if (taskEntry.cancelled()) {
            return false;
        }
        if (expirationMs < currentTime + tickMs) {
            return false;
        }
        if (expirationMs < currentTime + interval) {
            //Put in its own bucket
            long virtualId = expirationMs / tickMs;
            int index = (int) (virtualId % wheelSize);
            TimerTaskList bucket = this.getBucket(index);
            bucket.add(taskEntry);
            if (bucket.setExpiration(virtualId * tickMs)) {
                queue.offer(bucket);
            }
            return true;
        }
        if (overflowWheel == null) {
            addOverflowWheel();
        }
        return overflowWheel.add(taskEntry);
    }

    /**
     * Advance clock.
     *
     * @param timeMs the time ms
     */
    void advanceClock(final long timeMs) {
        if (timeMs >= currentTime + tickMs) {
            currentTime = timeMs - (timeMs % tickMs);
        }
        if (overflowWheel != null) {
            overflowWheel.advanceClock(currentTime);
        }
    }

    private TimerTaskList getBucket(final int index) {
        TimerTaskList bucket = buckets[index];
        if (bucket == null) {
            synchronized (this) {
                bucket = buckets[index];
                if (bucket == null) {
                    bucket = new TimerTaskList(taskCounter);
                    buckets[index] = bucket;
                }
            }
        }
        return bucket;
    }

}
