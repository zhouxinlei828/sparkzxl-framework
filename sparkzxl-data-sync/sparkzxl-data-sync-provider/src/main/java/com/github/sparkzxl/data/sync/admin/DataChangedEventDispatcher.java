package com.github.sparkzxl.data.sync.admin;

import com.github.sparkzxl.data.sync.admin.event.DataChangedEvent;
import com.github.sparkzxl.data.sync.admin.listener.DataChangedListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.*;

/**
 * description: Event forwarders, which forward the changed events to each ConfigEventListener.
 *
 * @author zhouxinlei
 * @since 2022-08-25 08:58:45
 */
public class DataChangedEventDispatcher implements ApplicationListener<DataChangedEvent>, InitializingBean {

    private final ApplicationContext applicationContext;

    private List<DataChangedListener> listeners;

    public DataChangedEventDispatcher(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(DataChangedEvent event) {
        for (DataChangedListener listener : listeners) {
            listener.onChanged(event.getGroupKey().getCode().toLowerCase(Locale.ROOT), event.getEventType().name(), event.getSource());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<DataChangedListener> listenerBeans = applicationContext.getBeansOfType(DataChangedListener.class).values();
        this.listeners = Collections.unmodifiableList(new ArrayList<>(listenerBeans));
    }
}