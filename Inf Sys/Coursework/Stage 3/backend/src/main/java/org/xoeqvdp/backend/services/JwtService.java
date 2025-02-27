package org.xoeqvdp.backend.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import org.xoeqvdp.backend.controllers.AuthController;
import org.xoeqvdp.backend.entities.Employee;
import org.xoeqvdp.backend.repositories.EmployeeRepository;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService{
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${token.signing.key}")
    private String SECRET_KEY;

    @Value("${token.expiration}")
    private int lifetime;

    private final EmployeeRepository employeeRepository;

    public JwtService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateAccessToken(Employee employee, List<String> roles) {
        return Jwts.builder()
                .subject(employee.getEmail())  // Используем email как subject
                .claim("id", employee.getId())  // Включаем id
                .claim("roles", roles)  // Включаем роли
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + lifetime))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Employee employee) {
        return Jwts.builder()
                .subject(employee.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 дней
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    public Instant getExpirationDate(String token) {
        return extractClaims(token).getExpiration().toInstant();
    }


    public List<String> getRolesFromToken(String token) {
        return extractClaims(token).get("roles", List.class);
    }

    public boolean validateAccessToken(String token){
        try {
            String email = getEmailFromToken(token);
            return employeeRepository.existsByEmail(email) && getExpirationDate(token).isAfter(Instant.now());
        }catch (Exception e){
            logger.warn("Failed on access token validation: {}", e.getMessage());
            return false;
        }

    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}