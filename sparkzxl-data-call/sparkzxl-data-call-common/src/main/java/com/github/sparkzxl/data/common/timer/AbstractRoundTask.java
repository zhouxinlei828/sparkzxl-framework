package com.github.sparkzxl.data.common.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description: AbstractRoundTask .
 * <p>A timer that runs periodically, repeatedly.<p/>
 *
 * @author zhouxinlei
 * @since 2022-08-25 13:35:39
 */
public abstract class AbstractRoundTask extends AbstractRetryTask {

    private final Logger logger = LoggerFactory.getLogger(AbstractRoundTask.class);

    /**
     * Instantiates a new Timer task.
     *
     * @param key     the key
     * @param delayMs the delay ms
     */
    public AbstractRoundTask(final String key, final long delayMs) {
        super(key, delayMs, -1);
    }

    @Override
    public void run(final TaskEntity taskEntity) {
        try {
            super.run(taskEntity);
        } finally {
            this.again(taskEntity);
        }
    }

    /**
     * Do retry.
     *
     * @param key       the key
     * @param timerTask the timer task
     */
    @Override
    protected void doRetry(final String key, final TimerTask timerTask) {
        try {
            this.doRun(key, timerTask);
        } catch (Throwable ex) {
            logger.warn("Failed to execute,but can be ignored");
        }
    }

    /**
     * Do timer.
     *
     * @param key       the key
     * @param timerTask the timer task
     */
    public abstract void doRun(String key, TimerTask timerTask);
}
