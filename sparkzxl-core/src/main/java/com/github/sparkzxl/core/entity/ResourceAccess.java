package com.github.sparkzxl.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description: 资源权限
 *
 * @author zhouxinlei
 */
@NoArgsConstructor
@Data
public class ResourceAccess {

    private AccountDTO account;

    @NoArgsConstructor
    @Data
    public static class AccountDTO {
        private List<String> roles;
    }

}
