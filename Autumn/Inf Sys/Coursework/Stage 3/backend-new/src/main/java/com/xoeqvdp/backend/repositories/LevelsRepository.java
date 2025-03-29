package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelsRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByWeight(Long weight);
}
