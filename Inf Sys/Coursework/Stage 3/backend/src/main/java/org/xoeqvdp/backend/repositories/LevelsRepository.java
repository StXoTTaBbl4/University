package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.xoeqvdp.backend.entities.Level;

import java.util.Optional;

@Repository
public interface LevelsRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByWeight(Long weight);
}
