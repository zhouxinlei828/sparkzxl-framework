package com.sparksys.commons.security.entity;

import com.google.common.collect.Lists;
import com.sparksys.commons.core.entity.GlobalAuthUser;
import com.sparksys.commons.core.utils.collection.ListUtils;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description：security用户
 *
 * @author： zhouxinlei
 * @date： 2020-06-24 16:35:11
 */
@Getter
public class AuthUserDetail implements UserDetails {

    private final GlobalAuthUser authUser;

    public AuthUserDetail(GlobalAuthUser authUser) {
        this.authUser = authUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (ListUtils.isNotEmpty(authUser.getPermissions())){
            return AuthorityUtils.createAuthorityList(ListUtils.listToString(authUser.getPermissions()));
        }
        return Lists.newArrayList();
    }

    @Override
    public String getPassword() {
        return authUser.getPassword();
    }

    @Override
    public String getUsername() {
        return authUser.getAccount();
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

    @Override
    public boolean isEnabled() {
        return authUser.getStatus();
    }
}