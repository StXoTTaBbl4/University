package com.xoeqvdp.backend.controllers;

import com.xoeqvdp.backend.dto.EmployeeSkillsDTO;
import com.xoeqvdp.backend.dto.ProfileInfoDTO;
import com.xoeqvdp.backend.entities.BlockType;
import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.repositories.EmployeeRepository;
import com.xoeqvdp.backend.repositories.SkillsRepository;
import com.xoeqvdp.backend.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final EmployeeRepository employeeRepository;
    private final SkillsRepository skillsRepository;

    private final JwtService jwtService;

    public ProfileController(EmployeeRepository employeeRepository, SkillsRepository skillsRepository, JwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.skillsRepository = skillsRepository;
        this.jwtService = jwtService;
    }


    @GetMapping("{employeeId}/info")
    public ResponseEntity<ProfileInfoDTO> getInfo(@RequestHeader(value = "Authorization") String authHeader, @PathVariable Long employeeId){
        Optional<Employee>  employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()){
            Employee e = employee.get();
            ProfileInfoDTO info = new ProfileInfoDTO(e.getName(), e.getEmail(), e.getId(), e.getRoles());
            return ResponseEntity.ok(info);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("{employeeId}/skills/{block}")
    public ResponseEntity<List<EmployeeSkillsDTO> > getSkills(@RequestHeader(value = "Authorization") String authHeader, @PathVariable Long employeeId, @PathVariable String block){
        Optional<Employee>  employee = employeeRepository.findById(employeeId);
        if (employee.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        System.out.println("employee found");
        System.out.println(BlockType.valueOf(block));
        List<EmployeeSkillsDTO> result =  skillsRepository.findAllByTask_Product_BlockTypeAndEmployee(BlockType.valueOf(block), employee.get());
        System.out.println(result);
        return ResponseEntity.ok().body(result);
    }



//    @GetMapping("/update/password")
//    public ResponseEntity<Object> updatePassword(@RequestHeader(value = "Authorization") String authHeader){
//
//    }
//
//    @GetMapping("/update/name")
//    public ResponseEntity<Object> updateName(){
//
//    }
}
