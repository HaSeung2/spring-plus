package org.example.expert.security;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(()-> user.getUserRole().name());
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    //계정이 만료되지 않았는지 리턴한다. ( true : 만료안됨)
    // 특별히 사용을 안할시 항상 true를 반환하도록 처리
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 비활성화 여부 제공
    // 특별히 사용 안할시 항상 true를 반환하도록 처리
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 인증 정보를 항상 저장할지에 대한 여부
    // true 처리할시 모든 인증정보를 만료시키지 않기에 주의해야한다.
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    //계정 활성화(사용가능)인지 리턴한다. ( true : 활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
