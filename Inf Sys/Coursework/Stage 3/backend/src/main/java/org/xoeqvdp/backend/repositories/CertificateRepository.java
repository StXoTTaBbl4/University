package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.xoeqvdp.backend.entities.Certificate;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByEmployeeId(Long employeeId); // Получить все сертификаты сотрудника
}
