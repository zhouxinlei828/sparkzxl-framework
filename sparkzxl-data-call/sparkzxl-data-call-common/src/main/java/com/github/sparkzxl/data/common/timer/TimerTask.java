package com.github.sparkzxl.data.common.timer;

import java.util.concurrent.TimeUnit;

/**
 * description: TimerTask.
 *
 * @author zhouxinlei
 * @since 2022-08-25 13:39:24
 */
public abstract class TimerTask {
    
    /**
     * The time the current task delays execution ms.
     */
    private final long delayMs;
    
    private TimerTaskList.TimerTaskEntry timerTaskEntry;
    
    /**
     * Instantiates a new Timer task.
     *
     * @param delayMs the delay ms
     */
    public TimerTask(final long delayMs) {
        this(delayMs, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Instantiates a new Timer task.
     *
     * @param delay the delay
     * @param unit  the unit
     */
    public TimerTask(final long delay, final TimeUnit unit) {
        delayMs = unit.toMillis(delay);
    }
    
    /**
     * Sets timer task entry.
     *
     * @param entry the entry
     */
    synchronized void setTimerTaskEntry(final TimerTaskList.TimerTaskEntry entry) {
        if (timerTaskEntry != null && timerTaskEntry != entry) {
            this.timerTaskEntry.remove();
        }
        timerTaskEntry = entry;
    }
    
    /**
     * Gets delay ms.
     *
     * @return the delay ms
     */
    long getDelayMs() {
        return delayMs;
    }
    
    /**
     * Gets timer task entry.
     *
     * @return the timer task entry
     */
    TimerTaskList.TimerTaskEntry getTimerTaskEntry() {
        return timerTaskEntry;
    }
    
    /**
     * Cancel task.
     */
    public synchronized void cancel() {
        if (timerTaskEntry != null) {
            timerTaskEntry.remove();
        }
        timerTaskEntry = null;
    }
    
    /**
     * Run.
     *
     * @param taskEntity the task entity
     */
    public abstract void run(TaskEntity taskEntity);
}
