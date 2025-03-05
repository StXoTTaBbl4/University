package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.CertificateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateCategoryRepository extends JpaRepository<CertificateCategory, Long> {
}
