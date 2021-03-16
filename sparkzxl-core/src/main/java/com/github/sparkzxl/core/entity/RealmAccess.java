package com.github.sparkzxl.core.entity;

import lombok.Data;

import java.util.List;

/**
 * description: 领域权限
 *
 * @author zhouxinlei
 */
@Data
public class RealmAccess {

    private List<String> roles;

}
