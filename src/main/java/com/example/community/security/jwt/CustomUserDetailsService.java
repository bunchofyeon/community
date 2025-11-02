package com.example.community.security.jwt;

import com.example.community.common.exception.custom.ResourceNotFoundException;
import com.example.community.entity.Users;
import com.example.community.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *  로그인 시 이메일로 DB에서 유저를 조회하는 클래스
 *  스프링 시큐리티가 로그인 시 호출하는 서비스
 *  이메일로 DB에서 유저를 조회해서 CustomUserDetails 형태로 반환
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    // 로그인 시 호출됨
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 이메일로 유저 조회
        // 없으면 예외 발생 (회원가입 안했거나 잘못된 이메일, 비밀번호 입력)
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("이메일과 비밀번호를 확인해주세요."));

        // 찾은 유저를 CustomUserDetails로 감싸서 반환
        return new CustomUserDetails(user);

    }
}
