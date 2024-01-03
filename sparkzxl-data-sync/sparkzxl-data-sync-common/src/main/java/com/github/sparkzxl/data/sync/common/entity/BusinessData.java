package com.github.sparkzxl.data.sync.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * description: Data set, including {{@link MetaData}.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:12:05
 */
@Data
@Accessors(chain = true)
public class BusinessData implements Serializable {

    private static final long serialVersionUID = -4252939968964886169L;
    private String md5;

    private long lastModifyTime;

    private List<Object> dataList;
}
