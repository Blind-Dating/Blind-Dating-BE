package com.blind.dating.security;

import com.blind.dating.domain.CustomUserDetails;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserRequestDto;
import com.blind.dating.util.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret_key}")
    private String SECRET_KEY;
    private Key key;
    private static final String AUTHORITIES_KEY = "auth";

    public String create(UserAccount userAccount){

        Date now = new Date();
        Date expiredAt = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        String authorities = new CustomUserDetails(userAccount).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(String.valueOf(userAccount.getId()))
                .setIssuer("blind dating web")
                .setIssuedAt(now)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshToken(UserAccount userAccount){

        Date now = new Date();
        Date expiredAt = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setSubject(String.valueOf(userAccount.getId()))
                .setIssuer("blind dating web")
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetUserId(String token) {
        //key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        try{

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        }catch (Exception e){
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }

    }

    public boolean validateToken(String token, HttpServletRequest request) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {

            log.info(ErrorCode.MALFORMED_TOKEN.getMessage());
            request.setAttribute("exception",ErrorCode.MALFORMED_TOKEN.getMessage());
            throw new JwtException(ErrorCode.MALFORMED_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {

            log.info(ErrorCode.EXPIRED_TOKEN.getMessage());
            request.setAttribute("exception",ErrorCode.EXPIRED_TOKEN.getMessage());
            throw new JwtException(ErrorCode.EXPIRED_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) {

            log.info(ErrorCode.UNSUPPORTED_TOKEN.getMessage());
            request.setAttribute("exception",ErrorCode.UNSUPPORTED_TOKEN.getMessage());
            throw new JwtException(ErrorCode.UNSUPPORTED_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {

            log.info(ErrorCode.WRONG_TYPE_TOKEN.getMessage());
            request.setAttribute("exception",ErrorCode.WRONG_TYPE_TOKEN.getMessage());
            throw new JwtException(ErrorCode.WRONG_TYPE_TOKEN.getMessage());
        }
    }

    public Authentication getAuthentication(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.getSubject();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userId, null, authorities);



    }



}
