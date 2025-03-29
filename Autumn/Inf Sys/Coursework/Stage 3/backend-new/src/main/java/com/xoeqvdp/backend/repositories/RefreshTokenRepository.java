package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.dto.RefreshTokenListDTO;
import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByEmployee(Employee employee);
    Optional<RefreshToken> findByEmployee(Employee employee);

    Optional<RefreshToken> findByEmployee_Id(Long id);

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT new com.xoeqvdp.backend.dto.RefreshTokenListDTO(t.employee.id, t.expiresAt, t.revoked) FROM RefreshToken t")
    List<RefreshTokenListDTO> getAllTokens();
}
