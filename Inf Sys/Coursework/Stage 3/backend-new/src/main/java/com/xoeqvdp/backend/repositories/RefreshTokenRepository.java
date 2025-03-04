package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByEmployee(Employee employee);
    Optional<RefreshToken> findByEmployee(Employee employee);
    Optional<RefreshToken> findByToken(String token);
}
