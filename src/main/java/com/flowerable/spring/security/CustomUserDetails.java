package com.flowerable.spring.security;

import com.flowerable.spring.constant.auth.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final Long accountId;
    private final Role role;

    public CustomUserDetails(Long accountId, Role role) {
        this.accountId = accountId;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) return List.of();
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    public Long getId() {
        return accountId;
    }
    public Role getRole() {
        return role;
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return accountId.toString(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
