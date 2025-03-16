package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.xoeqvdp.backend.entities.CertificateCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateCategoryRepository extends JpaRepository<CertificateCategory, Long> {
    @Query("SELECT c.name FROM CertificateCategory c")
    List<String> findAllNames();

}
