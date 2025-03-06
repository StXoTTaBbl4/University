package com.xoeqvdp.backend.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TaskAssignmentRepoClass {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void assignTaskToEmployees(Long employeeId, Long taskId, Long requiredLevelId, Long taskAssignId) {
        entityManager.createNativeQuery("CALL assign_task_to_employee(:p_employee_id, :p_task_id, :p_required_level_id, :p_task_assign_id)")
                .setParameter("p_employee_id", employeeId)
                .setParameter("p_task_id", taskId)
                .setParameter("p_required_level_id", requiredLevelId)
                .setParameter("p_task_assign_id", taskAssignId)
                .executeUpdate();
    }
}
