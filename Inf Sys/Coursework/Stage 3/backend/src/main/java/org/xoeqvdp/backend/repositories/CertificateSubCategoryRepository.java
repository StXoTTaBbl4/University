package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.xoeqvdp.backend.entities.CertificateCategory;
import org.xoeqvdp.backend.entities.CertificateSubCategory;

import java.util.List;

@Repository
public interface CertificateSubCategoryRepository extends JpaRepository<CertificateSubCategory, Long> {
    @Query("SELECT c.name FROM CertificateSubCategory c WHERE c.category =:category")
    List<String> findAllNames(CertificateCategory category);
}
