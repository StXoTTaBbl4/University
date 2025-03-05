package com.xoeqvdp.backend.controllers;

import com.xoeqvdp.backend.dto.SearchEmployeeDTO;
import com.xoeqvdp.backend.repositories.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class SearchController {
    private final EmployeeRepository employeeRepository;

    public SearchController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    @GetMapping("/load")
    public ResponseEntity<List<SearchEmployeeDTO>> loadAllEmployees(){
        return ResponseEntity.ok().body(employeeRepository.findAllByNameAndId());
    }
}
