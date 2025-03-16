package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.xoeqvdp.backend.entities.Employee;
import org.xoeqvdp.backend.entities.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmployee(Employee employee);
    boolean existsByEmployee(Employee employee);
}
