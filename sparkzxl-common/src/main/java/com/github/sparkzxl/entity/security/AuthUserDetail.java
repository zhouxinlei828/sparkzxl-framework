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
 * @since 2020-06-24 16:35:11
 */
@Getter
@Setter
public class AuthUserDetail extends User {

    private String id;

    private String tenantId;

    private String name;

    private boolean tenantStatus;

    public AuthUserDetail(String id, String username, String password, String name, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.name = name;
    }
}
