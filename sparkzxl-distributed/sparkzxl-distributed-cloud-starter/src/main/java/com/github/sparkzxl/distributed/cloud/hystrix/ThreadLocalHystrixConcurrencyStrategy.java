package com.github.sparkzxl.distributed.cloud.hystrix;

import com.github.sparkzxl.distributed.cloud.properties.CustomSeataProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import com.github.sparkzxl.distributed.cloud.utils.ThreadLocalUtils;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: 本地线程 Hystrix并发策略
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 16:29:54
 */
@Slf4j
public class ThreadLocalHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    private HystrixConcurrencyStrategy delegate;

    private final CustomSeataProperties customSeataProperties;

    public ThreadLocalHystrixConcurrencyStrategy(CustomSeataProperties customSeataProperties) {
        this.customSeataProperties = customSeataProperties;
        try {
            this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
            if (this.delegate instanceof ThreadLocalHystrixConcurrencyStrategy) {
                // Welcome to singleton hell...
                return;
            }
            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins
                    .getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance()
                    .getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance()
                    .getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance()
                    .getPropertiesStrategy();
            this.logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher,
                    propertiesStrategy);
            HystrixPlugins.reset();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
            HystrixPlugins.getInstance()
                    .registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        } catch (Exception e) {
            log.error("Failed to register Sleuth Hystrix Concurrency Strategy", e);
        }
    }

    private void logCurrentStateOfHystrixPlugins(HystrixEventNotifier eventNotifier,
                                                 HystrixMetricsPublisher metricsPublisher,
                                                 HystrixPropertiesStrategy propertiesStrategy) {
        if (log.isDebugEnabled()) {
            log.debug("Current Hystrix plugins configuration is [concurrencyStrategy [{}],eventNotifier [{}],metricPublisher [{}],propertiesStrategy [{}],]",
                    this.delegate,
                    eventNotifier,
                    metricsPublisher,
                    propertiesStrategy);
        }
    }

    /**
     * 复制当前线程中的 requestAttributes 和 localMap， 注入到装饰器： WrappedCallable
     *
     * @param callable
     * @param <T>
     * @return
     */
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        if (callable instanceof WrappedCallable) {
            return callable;
        }
        Callable<T> wrappedCallable = this.delegate != null
                ? this.delegate.wrapCallable(callable) : callable;
        if (wrappedCallable instanceof WrappedCallable) {
            return wrappedCallable;
        }

        return new WrappedCallable<>(callable, customSeataProperties);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixThreadPoolProperties threadPoolProperties) {
        return this.delegate.getThreadPool(threadPoolKey, threadPoolProperties);
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return this.delegate.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(
            HystrixRequestVariableLifecycle<T> rv) {
        return this.delegate.getRequestVariable(rv);
    }

    static class WrappedCallable<T> implements Callable<T> {

        private final Callable<T> target;
        private final RequestAttributes requestAttributes;
        private final Map<String, String> threadLocalMap; //研究并发是否会冲突
        private final String xid;
        private final CustomSeataProperties customSeataProperties;

        WrappedCallable(Callable<T> target, CustomSeataProperties customSeataProperties) {
            this.target = target;
            this.customSeataProperties = customSeataProperties;
            this.requestAttributes = RequestContextHolder.getRequestAttributes();
            this.threadLocalMap = ThreadLocalUtils.getLocalMap();
            if (this.customSeataProperties.isEnable()){
                this.xid = RootContext.getXID();
            }else {
                this.xid = null;
            }
        }

        @Override
        public T call() throws Exception {
            try {
                RequestContextHolder.setRequestAttributes(this.requestAttributes);
                ThreadLocalUtils.setLocalMap(this.threadLocalMap);
                if (this.customSeataProperties.isEnable()){
                    RootContext.bind(this.xid);
                }
                return this.target.call();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                ThreadLocalUtils.remove();
                if (this.customSeataProperties.isEnable()){
                    RootContext.unbind();
                }
            }
        }
    }
}
