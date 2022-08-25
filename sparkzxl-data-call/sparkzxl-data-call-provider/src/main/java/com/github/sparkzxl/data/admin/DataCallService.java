package com.github.sparkzxl.data.admin;


import com.github.sparkzxl.data.common.enums.DataEventTypeEnum;

/**
 * description: The interface Call data service.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:06:14
 */
public interface DataCallService {
    
    /**
     * Sync all boolean.
     *
     * @param type the type
     * @return the boolean
     */
    boolean syncAll(DataEventTypeEnum type);

}
