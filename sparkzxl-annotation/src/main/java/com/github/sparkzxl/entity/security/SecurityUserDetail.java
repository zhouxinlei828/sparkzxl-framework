package com.github.sparkzxl.entity.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * description: 认证用户
 *
 * @author zhouxinlei
 * @since 2021-07-10 14:30:00
 */
@Data
public class SecurityUserDetail<T> implements UserDetails {

    private static final long serialVersionUID = -8178549809025677329L;

    private T id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户名
     */
    private String name;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户状态
     */
    private Boolean enabled;
    /**
     * 权限数据
     */
    private Collection<SimpleGrantedAuthority> authorities;

    private List<String> authorityList;

    public SecurityUserDetail(T id, String username, String password, String name, Boolean enabled, List<String> authorityList) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.enabled = enabled;
        this.authorityList = authorityList;
        if (!CollectionUtils.isEmpty(authorityList)) {
            this.authorities = new ArrayList<>();
            authorityList.forEach(item -> this.authorities.add(new SimpleGrantedAuthority(item)));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}