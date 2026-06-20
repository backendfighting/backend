package com.fighting.goaltracker.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 토큰 서명에 쓰일 비밀키 (서버에서만 알고 있어야 함)
    private final String SECRET = "goaltracker-secret-key-for-jwt-token-must-be-long-enough-32bytes!";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    // 토큰 유효 시간: 1일 (밀리초 단위)
    private final long expirationTime = 1000 * 60 * 60 * 24;

    // 토큰 발급
    public String generateToken(Integer userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 userId 꺼내기
    public Integer extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("userId", Integer.class);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}