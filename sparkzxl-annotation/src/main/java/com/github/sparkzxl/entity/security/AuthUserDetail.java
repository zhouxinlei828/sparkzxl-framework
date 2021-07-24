package com.github.sparkzxl.entity.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * description：security用户
 *
 * @author charles.zhou
 * @date 2020-06-24 16:35:11
 */
@Getter
@Setter
public class AuthUserDetail<T> extends User {

    private T id;

    private String tenant;

    private String name;

    private boolean tenantStatus;

    public AuthUserDetail(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
