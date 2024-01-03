package com.github.sparkzxl.data.sync.common.entity;

import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;

import java.io.Serializable;
import java.util.List;

/**
 * description:Data set, including {@link MetaData}.
 *
 * @author zhouxinlei
 * @since 2022-08-25 10:54:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PushData<T> implements Serializable {

    private static final long serialVersionUID = -9088425862273081300L;
    /**
     * synchronization id
     */
    private String syncId;

    /**
     * config group. {@linkplain ConfigGroupEnum}
     */
    private String configGroup;

    /**
     * event type. {@linkplain DataEventTypeEnum}
     */
    private String eventType;

    /**
     * data list. {@link MetaData}.
     */
    private List<T> data;

}
