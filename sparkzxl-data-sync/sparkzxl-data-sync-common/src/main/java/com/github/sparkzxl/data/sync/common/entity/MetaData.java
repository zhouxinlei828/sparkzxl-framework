package com.github.sparkzxl.data.sync.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * description:  租户元数据实体对象
 *
 * @author zhouxinlei
 * @since 2022-08-24 10:40:34
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class MetaData implements Serializable {

    private static final long serialVersionUID = -1647129778629174524L;

    /**
     * MetaData id
     */
    private String id;

    /**
     * 租户id
     */
    private String tenantId;


    /**
     * 地区code
     */
    private String areaCode;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 数据类型描述
     */
    private String dataTypeDesc;

    /**
     * 属性code
     */
    private String dataCode;

    /**
     * 数据名
     */
    private String dataName;

    /**
     * 数据描述
     */
    private String dataDesc;

    /**
     * 数据值
     */
    private String dataValue;

    /**
     * 数据值扩展
     */
    private String dataValueExt;

    /**
     * 是否启用
     */
    private boolean enabled;
}
