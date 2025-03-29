package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.CertificateSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateSubCategoryRepository extends JpaRepository<CertificateSubCategory, Long> {
    Optional<CertificateSubCategory> findByNameAndCategory_Id(String name, Long id);
}
