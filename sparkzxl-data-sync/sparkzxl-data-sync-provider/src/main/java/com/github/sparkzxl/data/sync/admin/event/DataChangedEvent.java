package com.github.sparkzxl.data.sync.admin.event;

import com.github.sparkzxl.data.sync.admin.DataChangedEventDispatcher;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEvent;

/**
 * description: Data change event.
 *
 * @author zhouxinlei
 * @see DataChangedEventDispatcher
 * @since 2022-08-25 09:00:50
 */
public class DataChangedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 8717534518634182507L;

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
