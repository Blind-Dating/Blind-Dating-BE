package com.blind.dating.security;

import com.blind.dating.domain.UserAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenProvider {

    private static final String SECRET_KEY = "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";
    private Key key;

    public String create(UserAccount userAccount){

        Date now = new Date();
        Date expiredAt = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Claims claims = Jwts.claims();

        return Jwts.builder()
                .setSubject(userAccount.getUserId())
                .setIssuer("blind dating web")
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetUserId(String token) throws JsonProcessingException {
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Jwt jwt = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parse(token);

        Claims claims = (Claims)jwt.getBody();

        return claims.get("sub", String.class);
    }



}