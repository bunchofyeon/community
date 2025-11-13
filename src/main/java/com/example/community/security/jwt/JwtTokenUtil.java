package com.example.community.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static com.example.community.common.AuthConstants.CLAIM_ROLE;

/**
 * 토큰 발급/검증
 */
@Component
public class JwtTokenUtil implements Serializable {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtTokenUtil(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.token-expiration-seconds}") long expirationSeconds
    ) {

        // Base64 시크릿(yml에 있는 문자열)을 바이트로 복원
        byte[] raw = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));

        this.key = Keys.hmacShaKeyFor(raw);
        this.expirationMillis = expirationSeconds * 1000L; // 밀리초로 바꾸기
    }

    // 1. Access Token 생성
    public String generateToken(String email, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email) // 토큰 주인 (이메일)
                .claim(CLAIM_ROLE, role) // 추가 정보
                .setIssuedAt(new Date(now)) // 발급 시각
                .setExpiration(new Date(now + expirationMillis)) // 만료 시간 (언제 만료될지)
                .signWith(key, SignatureAlgorithm.HS512) // 서명
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
        return Jwts.parserBuilder()
                .setSigningKey(key) // 서명 검증용 키 등록
                .build() // parser 객체 생성
                .parseClaimsJws(token) // 토큰이 진짜인지 파싱 + 검증
                .getBody(); // Payload(Claims, 유저 정보)등 추출
    }
}
