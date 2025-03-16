package com.xoeqvdp.backend.controllers;

import com.xoeqvdp.backend.dto.EmployeeSkillsDTO;
import com.xoeqvdp.backend.dto.FilteredByScoreEmployeesDTO;
import com.xoeqvdp.backend.dto.LeaderSearchFiltersDTO;
import com.xoeqvdp.backend.entities.BlockType;
import com.xoeqvdp.backend.repositories.ProductsRepository;
import com.xoeqvdp.backend.repositories.SkillsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leader")
public class LeaderController {

    private final SkillsRepository skillsRepository;

    public LeaderController( SkillsRepository skillsRepository) {
        this.skillsRepository = skillsRepository;
    }

    @PostMapping("/search/filterEmployees")
    public ResponseEntity<List<FilteredByScoreEmployeesDTO>> getSearchOptions(@RequestBody LeaderSearchFiltersDTO filters){
        return ResponseEntity.ok().body(skillsRepository.getFilteredByScore(filters.getHw(), filters.getSw(), filters.getPr()));
    }
}
