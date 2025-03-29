package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.xoeqvdp.backend.dto.CertificatesResponse;
import org.xoeqvdp.backend.entities.Certificate;
import org.xoeqvdp.backend.entities.Employee;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    @Query("SELECT new org.xoeqvdp.backend.dto.CertificatesResponse(c.name, c.id) FROM Certificate c WHERE c.employee = :employee")
    List<CertificatesResponse> findByEmployee(Employee employee);
}
