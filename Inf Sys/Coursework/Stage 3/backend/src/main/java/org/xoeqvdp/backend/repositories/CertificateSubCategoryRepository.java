package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.xoeqvdp.backend.entities.CertificateSubCategory;

@Repository
public interface CertificateSubCategoryRepository extends JpaRepository<CertificateSubCategory, Long> {}
