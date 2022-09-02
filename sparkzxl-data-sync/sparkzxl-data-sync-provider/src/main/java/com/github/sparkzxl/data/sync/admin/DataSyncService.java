package com.github.sparkzxl.data.sync.admin;


import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;

/**
 * description: The interface sync data service.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:06:14
 */
public interface DataSyncService {
    
    /**
     * Sync all boolean.
     *
     * @param type the type
     * @return the boolean
     */
    boolean syncAll(DataEventTypeEnum type);

}
