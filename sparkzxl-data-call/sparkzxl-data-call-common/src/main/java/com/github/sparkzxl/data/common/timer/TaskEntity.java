package com.github.sparkzxl.data.common.timer;

/**
 * description: TimerEntity .
 *
 * @author zhouxinlei
 * @since 2022-08-25 13:38:53
 */
public interface TaskEntity {
    
    /**
     * Gets timer.
     *
     * @return the timer
     */
    Timer getTimer();
    
    /**
     * Gets timer task.
     *
     * @return the timer task
     */
    TimerTask getTimerTask();
    
    /**
     * Cancelled boolean.
     *
     * @return the boolean
     */
    boolean cancelled();
    
    /**
     * Cancel boolean.
     */
    void cancel();
}
