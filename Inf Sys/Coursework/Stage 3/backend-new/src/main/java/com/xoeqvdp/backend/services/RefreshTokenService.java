package com.xoeqvdp.backend.services;

import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.entities.RefreshToken;
import com.xoeqvdp.backend.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void saveOrUpdateRefreshToken(Employee employee, String token, Instant expiresAt) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByEmployee(employee);

        if (existingToken.isPresent()) {
            // Обновляем существующий токен
            RefreshToken refreshToken = existingToken.get();
            refreshToken.setToken(token);
            refreshToken.setExpiresAt(expiresAt);
            refreshToken.setRevoked(false); // Делаем активным, если вдруг был отозван
            refreshTokenRepository.save(refreshToken);
        } else {
            // Создаём новый токен
            RefreshToken newToken = new RefreshToken(employee, token, expiresAt, false, Instant.now());
            refreshTokenRepository.save(newToken);
        }
    }

    public Boolean isRefreshTokenRevoked(String refreshToken){
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            return token.get().isRevoked();
        } else {
            return null;
        }
    }
}
