package com.sparksys.commons.mybatis.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * description: 性别枚举
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:21:59
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Sex implements BaseEnum {


    W("女"),

    M("男"),

    N("未知"),
    ;

    private String desc;


    public static Sex match(String val, Sex def) {
        for (Sex enm : Sex.values()) {
            if (enm.name().equalsIgnoreCase(val)) {
                return enm;
            }
        }
        return def;
    }

    public static Sex matchDesc(String val, Sex def) {
        for (Sex enm : Sex.values()) {
            if (enm.getDesc().equalsIgnoreCase(val)) {
                return enm;
            }
        }
        return def;
    }

    public static Sex get(String val) {
        return match(val, null);
    }

    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(Sex val) {
        if (val == null) {
            return false;
        }
        return eq(val.name());
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
