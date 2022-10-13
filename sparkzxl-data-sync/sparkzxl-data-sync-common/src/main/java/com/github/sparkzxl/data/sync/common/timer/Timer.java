package com.github.sparkzxl.data.sync.common.timer;

/**
 * description: Timer .
 *
 * @author zhouxinlei
 * @since 2022-08-25 13:39:07
 */
public interface Timer {

    /**
     * Add timer task.
     *
     * @param timerTask the timer task
     */
    void add(TimerTask timerTask);

    /**
     * Advance clock boolean.
     *
     * @param timeoutMs the timeout ms
     * @throws InterruptedException the interrupted exception
     */
    void advanceClock(long timeoutMs) throws InterruptedException;

    /**
     * Size int.
     *
     * @return the int
     */
    int size();

    /**
     * Shutdown.
     */
    void shutdown();

}

