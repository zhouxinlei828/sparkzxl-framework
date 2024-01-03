package com.github.sparkzxl.data.sync.admin.listener;

import com.github.sparkzxl.data.sync.admin.DataSyncService;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;

import javax.annotation.Resource;

public abstract class AbstractDataChangedInit implements DataChangedInit {

    @Resource
    private DataSyncService dataSyncService;

    @Override
    public void run(final String... args) throws Exception {
        if (notExist()) {
            dataSyncService.syncAll(DataEventTypeEnum.REFRESH);
        }
    }

    /**
     * check exist.
     *
     * @return boolean.
     */
    protected abstract boolean notExist();

}
