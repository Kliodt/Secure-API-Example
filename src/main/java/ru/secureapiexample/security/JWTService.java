package ru.secureapiexample.security;


import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;


@Service
public class JWTService {

    @Value("${spring.security.jwt.secret_key}")
    private String secretKey; // key to encrypt jwt

    @Value("${spring.security.jwt.access_token_lifetime_ms}")
    private long accessTokenLifetimeMs;


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String createAccessToken(long userId) {
        return "Bearer " + Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenLifetimeMs))
                .signWith(getSecretKey())
                .compact();
    }


    public Optional<Long> parseToken(String token) {
        if (!token.startsWith("Bearer ")) return Optional.empty();
        token = token.substring(7); // remove 'Bearer' prefix

        JwtParser parser = Jwts.parserBuilder().setSigningKey(getSecretKey()).build();

        try {
            var sub = parser.parseClaimsJws(token).getBody().getSubject();
            return Optional.of(Long.parseLong(sub));
        } catch (Exception e) { // expired or invalid token
            return Optional.empty();
        }
    }
}
