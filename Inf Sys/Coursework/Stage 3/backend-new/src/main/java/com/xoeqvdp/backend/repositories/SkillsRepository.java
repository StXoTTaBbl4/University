package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.dto.EmployeeSkillsDTO;
import com.xoeqvdp.backend.dto.FilteredByScoreEmployeesDTO;
import com.xoeqvdp.backend.entities.BlockType;
import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.entities.EmployeeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SkillsRepository extends JpaRepository<EmployeeSkill, Long> {


    @Query(value = "SELECT * FROM get_employees_with_block_points_above_limit(:hw, :sw, :pr)", nativeQuery = true)
    List<FilteredByScoreEmployeesDTO> getFilteredByScore(Integer hw, Integer sw, Integer pr);

    @Query("SELECT new com.xoeqvdp.backend.dto.EmployeeSkillsDTO(s.task.product.product, s.task.task, s.level.level) FROM EmployeeSkill s where s.task.product.blockType = :blockType AND s.employee= :employee")
    List<EmployeeSkillsDTO> findAllByTask_Product_BlockTypeAndEmployee(BlockType blockType, Employee employee);
}
