package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.dto.EmployeeRolesDTO;
import com.xoeqvdp.backend.dto.SearchEmployeeDTO;
import com.xoeqvdp.backend.entities.Employee;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    @Query("SELECT new com.xoeqvdp.backend.dto.SearchEmployeeDTO(e.id, e.name, e.email) FROM Employee e")
    List<SearchEmployeeDTO> findAllByNameAndId();
}
