package com.golden.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(Long userId, String username) {
        logger.debug("生成JWT Token: userId={}, username={}", userId, username);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
        
        logger.debug("JWT Token生成成功: userId={}, expiresAt={}", userId, expiryDate);
        return token;
    }
    
    public Long getUserIdFromToken(String token) {
        try {
            logger.debug("解析JWT Token获取用户ID");
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Long userId = Long.parseLong(claims.getSubject());
            logger.debug("JWT Token解析成功: userId={}", userId);
            return userId;
        } catch (Exception e) {
            logger.error("JWT Token解析失败: error={}", e.getMessage(), e);
            throw new RuntimeException("Token无效", e);
        }
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            logger.debug("JWT Token验证成功");
            return true;
        } catch (Exception e) {
            logger.warn("JWT Token验证失败: error={}", e.getMessage());
            return false;
        }
    }
}

