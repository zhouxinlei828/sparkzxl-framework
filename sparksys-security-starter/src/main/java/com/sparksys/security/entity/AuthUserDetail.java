package com.sparksys.security.entity;

import com.google.common.collect.Lists;
import com.sparksys.core.entity.AuthUserInfo;
import com.sparksys.core.utils.ListUtils;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * description：security用户
 *
 * @author： zhouxinlei
 * @date： 2020-06-24 16:35:11
 */
@Getter
public class AuthUserDetail implements UserDetails {

    private final AuthUserInfo authUserInfo;

    public AuthUserDetail(AuthUserInfo authUserInfo) {
        this.authUserInfo = authUserInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (ListUtils.isNotEmpty(authUserInfo.getAuthorityList())){
            return AuthorityUtils.createAuthorityList(ListUtils.listToString(authUserInfo.getAuthorityList()));
        }
        return Lists.newArrayList();
    }

    @Override
    public String getPassword() {
        return authUserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return authUserInfo.getAccount();
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
        return authUserInfo.getStatus();
    }
}
