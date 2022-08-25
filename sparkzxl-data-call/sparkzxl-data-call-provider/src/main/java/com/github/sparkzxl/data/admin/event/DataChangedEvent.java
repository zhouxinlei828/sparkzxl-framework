package com.github.sparkzxl.data.admin.event;

import com.github.sparkzxl.data.admin.DataChangedEventDispatcher;
import com.github.sparkzxl.data.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.data.common.enums.DataEventTypeEnum;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * description: Data change event.
 * @see DataChangedEventDispatcher
 * @author zhouxinlei
 * @since 2022-08-25 09:00:50
 */
public class DataChangedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -6567019416534398513L;

    private final DataEventTypeEnum eventType;

    private final ConfigGroupEnum groupKey;

    /**
     * Instantiates a new Data changed event.
     *
     * @param groupKey the group key
     * @param type     the type
     * @param source   the source
     */
    public DataChangedEvent(final ConfigGroupEnum groupKey, final DataEventTypeEnum type, final List<?> source) {
        super(source.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        this.eventType = type;
        this.groupKey = groupKey;
    }

    /**
     * Gets event type.
     *
     * @return the event type
     */
    public DataEventTypeEnum getEventType() {
        return eventType;
    }

    @Override
    public List<?> getSource() {
        return (List<?>) super.getSource();
    }

    /**
     * Gets group key.
     *
     * @return the group key
     */
    public ConfigGroupEnum getGroupKey() {
        return this.groupKey;
    }

}
