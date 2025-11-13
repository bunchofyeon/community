package com.example.community.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.community.common.AuthConstants.AUTHORIZATION_HEADER;
import static com.example.community.common.AuthConstants.BEARER_PREFIX;


/**
 * JWT 토큰 검증을 수행하는 필터
 * (요청이 들어올 때마다 실행됨)
 * */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(AUTHORIZATION_HEADER);

        // 헤더가 없거나 Bearer 토큰이 아니면 다음 필터로
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 다음부터가 실제 토큰 문자열 이라서...
        String token = header.substring(7);

        // 토큰이 유효하면 SecurityContext에 인증 정보 저장
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            System.out.println("JWT token email = " + email); // 잘 들어왔나 확인용..
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email); // 이메일로 유저 찾기

            // 스프링 시큐리티가 이해하는 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()); // 누구인지, 자격증명(null), 권한

            // 요청 관련 부가정보(IP, 세션 등) 붙이기
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // 현재 요청의 보안 컨텍스트에 인증됨 상태로 저장 → 이후 @AuthenticationPrincipal 사용 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }
}
