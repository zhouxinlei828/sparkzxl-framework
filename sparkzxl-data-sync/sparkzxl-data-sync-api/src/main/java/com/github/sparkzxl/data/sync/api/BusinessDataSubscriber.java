package com.github.sparkzxl.data.sync.api;


import com.github.sparkzxl.data.sync.common.entity.BusinessData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;

/**
 * description: The interface business data subscriber.
 *
 * @author zhouxinlei
 * @since 2022-08-24 10:49:25
 */
public interface BusinessDataSubscriber extends DataSubscriber<BusinessData> {

    /**
     * data group
     *
     * @return String
     * @see ConfigGroupEnum
     */
    @Override
    default String group() {
        return ConfigGroupEnum.META_DATA.getCode();
    }
}
