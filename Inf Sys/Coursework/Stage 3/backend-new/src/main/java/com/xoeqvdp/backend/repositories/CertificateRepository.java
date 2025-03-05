package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.dto.CertificatesInfoResponseDTO;
import com.xoeqvdp.backend.dto.CertificatesResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.xoeqvdp.backend.entities.Certificate;
import com.xoeqvdp.backend.entities.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    @Query("SELECT new com.xoeqvdp.backend.dto.CertificatesResponseDTO(c.name, c.id, c.subCategory.category.name, c.subCategory.name) FROM Certificate c WHERE c.employee = :employee")
    List<CertificatesResponseDTO> findByEmployee(Employee employee);

    @Query("SELECT new com.xoeqvdp.backend.dto.CertificatesInfoResponseDTO(c.name, c.id, c.subCategory.category.name, c.subCategory.name, c.employee.name) FROM Certificate c WHERE c.id=:id AND c.employee.email = :email")
    Optional<CertificatesInfoResponseDTO> findByIdAndEmployee_Email(Long id, String email);

    boolean existsByEmployeeAndId(Employee employee, Long id);
}
