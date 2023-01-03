package com.ifans.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ifans.api.system.model.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDetails implements UserDetails {

    private LoginUser loginUser;

    public LoginUserDetails(LoginUser loginUser) {
        this.loginUser = loginUser;
        //this.loginUser.getSysUser().setPassword("{bcrypt}" + this.loginUser.getSysUser().getPassword());
    }

    @JsonIgnore
    private List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = loginUser.getPermissions().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.loginUser.getSysUser().getPassword();
    }

    @Override
    public String getUsername() {
        return this.loginUser.getSysUser().getUserName();
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
