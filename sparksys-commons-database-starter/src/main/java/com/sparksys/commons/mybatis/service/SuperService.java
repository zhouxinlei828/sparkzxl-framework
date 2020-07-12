package com.sparksys.commons.mybatis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.sparksys.commons.core.support.ResponseResultStatus;
import com.sparksys.commons.mybatis.mapper.SuperMapper;

import java.util.List;
/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-07-07 20:26:15
*/
public interface SuperService<T> extends IService<T> {

    /**
     * 批量保存
     *
     * @param entityList 实体对象
     * @return boolean
     */
    default boolean saveBatchSomeColumn(List<T> entityList) {
        if (entityList.isEmpty()) {
            return true;
        } else if (entityList.size() > 5000) {
            ResponseResultStatus.TOO_MUCH_DATA_ERROR.newException(entityList);
        } else {
            return SqlHelper.retBool(((SuperMapper<T>) this.getBaseMapper()).insertBatchSomeColumn(entityList));
        }
        return false;
    }

}
