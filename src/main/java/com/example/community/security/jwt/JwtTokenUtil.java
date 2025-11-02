package com.example.community.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * 토큰 발급/검증
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final String SECRET_KEY = "7YyM7J2064uI44WL7YyM7J207YyFISHtlaDsiJjsnojslrQhIey9lOuqveydtOuaseydtOu0ieu0ieydtOynsQ==";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간

    // 1. Access Token 생성
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email) // 토큰 주인 (이메일)
                .claim("role", role) // 추가 정보
                .setIssuedAt(new Date()) // 발급 시각
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간 (언제 만료될지)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // 서명
                .compact();
    }

    // 2. 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // 3. 토큰 유효성 검증 (토큰이 진짜인지/만료 되었는지)
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token); // 토큰 안의 데이터(Claims) 꺼내기
            return !claims.getExpiration().before(new Date()); // 만료시간 가져오기 -> 근데 지금보다 이전이면 이미 만료된걸로 판단
        } catch (Exception e) {
            return false; // 만료(만료 시간이 지났음)
        }
    }

    // 4. 토큰 검증 및 내용 추출 (토큰 파싱)
    // 토큰을 열어서 안에 들어있는 이메일, role, 만료시간 같은 내용을 꺼내는 함수
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY) // 검증
                .parseClaimsJws(token) // 토큰 복호화
                .getBody(); // Payload(Claims) 부분만 꺼냄

    }

}
