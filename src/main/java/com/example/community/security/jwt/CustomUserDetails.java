package com.example.community.security.jwt;

import com.example.community.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 *  로그인한 사용자 한 명의 정보를 스프링 시큐리티가 인식할 수 있게 감싸는 클래스
 */

@Getter
public class CustomUserDetails implements UserDetails {

    private final Users users; // 로그인한 사용자 엔티티 저장

    public CustomUserDetails(Users users) {
        this.users = users;
    }

    // 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + users.getRole().name()));
    }

    // 비밀번호 반환
    @Override
    public String getPassword() {
        return users.getPassword();
    }

    // 이메일로 로그인 -> 이메일 반환
    @Override
    public String getUsername() {
        return users.getEmail();
    }

    // 계정 만료 여부 (true면 만료 안됨)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        // return true;
        return users.getDeletedAt() == null; // soft delete -> 탈퇴/삭제된 계정은 비활성
    }

}
