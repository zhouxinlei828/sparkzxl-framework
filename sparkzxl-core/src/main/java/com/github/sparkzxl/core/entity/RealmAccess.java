package com.github.sparkzxl.core.entity;

import lombok.Data;

import java.util.List;

/**
 * description: 领域权限
 *
 * @author: zhouxinlei
 * @date: 2021-01-30 11:26:59
 */
@Data
public class RealmAccess {

    private List<String> roles;

}
