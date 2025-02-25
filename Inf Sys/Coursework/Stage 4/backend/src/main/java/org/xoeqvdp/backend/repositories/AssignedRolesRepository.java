package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.xoeqvdp.backend.entities.AssignedRoles;

import java.util.List;

@Repository
public interface AssignedRolesRepository extends JpaRepository<AssignedRoles, Long> {

    @Query("SELECT r.role FROM AssignedRoles r  WHERE r.employee = :employeeId")
    List<String> findRolesByEmployeeID(Long employeeId);
}