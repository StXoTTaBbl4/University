package com.xoeqvdp.backend.services;

import com.xoeqvdp.backend.entities.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final RefreshTokenService refreshTokenService;

    //Уиииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииии Ты зачем долистал 0_0
    private static final String SECRET_KEY = "hw3UqTN8NdxEMCnBVdy6m2xPYCE2WAuRPvNNUetj5H6kGBf6hdaj4PncLNtFzzuZjad4eZfdw3vUM5LcB7tpPsmG6cXa35aSJERvgTNW3qTq2b5HwtmJdBRLxp5dJZ2vaw2NZpRwrZDbgwrxPgXZmQq8Gz7HAauY647by6nF3nc9fsM4a2bHnTvpGLsMUntmjRTrFHppmD2A9DuCTEyYNj8rG3f999xRzmVSxarVzrAx5qdAkyM2snmxaekuetU8";
    private final Long ACCESS_EXPIRATION = 900000L;
    private final Integer REFRESH_EXPIRATION = 604800000;

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public JwtService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }


    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        String token = generateToken(userDetails, Long.valueOf(REFRESH_EXPIRATION));
        refreshTokenService.saveOrUpdateRefreshToken((Employee) userDetails, token, getExpirationDate(token));
        return token;
    }

    public String generateToken(UserDetails userDetails, Long expiration) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(KEY, SignatureAlgorithm.HS256) // Используем `KEY`
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(KEY) // Используем тот же ключ
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public Long getAccessTokenExpirationPeriod(){
        return ACCESS_EXPIRATION;
    }

    public Integer getRefreshTokenExpirationPeriod(){
        return REFRESH_EXPIRATION;
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Instant getExpirationDate(String token) {
        return extractClaims(token).getExpiration().toInstant();
    }

}