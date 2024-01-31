package com.swifty.bank.server.core.customer.dto;

import com.swifty.bank.server.core.config.constant.UserRole;
import com.swifty.bank.server.core.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.customer.constant.Nationality;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class CustomerDto implements UserDetails {
    private UUID id;    // PK
    private String name;
    private String socialToken;
    private String phoneNumber;
    private Nationality nationality;
    private CustomerStatus customerStatus;  // 휴면 상태, 정지된 사용자 등
    private String deviceId;
    private String password;
    private UserRole role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role.getRole()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
