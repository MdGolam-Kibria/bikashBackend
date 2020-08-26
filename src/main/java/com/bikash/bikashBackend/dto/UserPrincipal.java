package com.bikash.bikashBackend.dto;

import com.bikash.bikashBackend.Model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserPrincipal implements UserDetails {
    private Long id;
    private String phone;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;//role

    public UserPrincipal(Long id, String phone, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        try {
            List<GrantedAuthority> authority = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
            return new UserPrincipal(user.getId(), user.getPhone(), user.getPassword(), authority);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phone;
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
        return true;
    }
}
