package com.xoeqvdp.backend.controllers;

import com.xoeqvdp.backend.dto.EmployeeRolesDTO;
import com.xoeqvdp.backend.dto.RefreshTokenListDTO;
import com.xoeqvdp.backend.dto.RoleRequestDTO;
import com.xoeqvdp.backend.dto.TokenRequestDTO;
import com.xoeqvdp.backend.entities.AccountRoles;
import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.entities.RefreshToken;
import com.xoeqvdp.backend.repositories.EmployeeRepository;
import com.xoeqvdp.backend.repositories.RefreshTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmployeeRepository employeeRepository;

    public AdminController(RefreshTokenRepository refreshTokenRepository, EmployeeRepository employeeRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.employeeRepository = employeeRepository;
    }


    @GetMapping("/tokens")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public List<RefreshTokenListDTO> getTokenList(){
        return refreshTokenRepository.getAllTokens();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public List<EmployeeRolesDTO> getEmployeesRolesList(){
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeRolesDTO> rolesDTO = new ArrayList<>();

        for(Employee e: employees){
            rolesDTO.add(new EmployeeRolesDTO(e.getId(), e.getRoles()));
        }

        return rolesDTO;
    }

    @PostMapping("/revokeToken")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Object> revokeRefreshToken(@RequestBody TokenRequestDTO request){
        System.out.println(request.getEmployeeId());
        Optional<RefreshToken> token = refreshTokenRepository.findByEmployee_Id(request.getEmployeeId());
        if (token.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        token.get().setRevoked(true);
        refreshTokenRepository.save(token.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addRole")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Object> addRole(@RequestBody RoleRequestDTO request){
        Optional<Employee> employee = employeeRepository.findById(request.getEmployeeId());
        if (employee.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        employee.get().getRoles().add(AccountRoles.valueOf(request.getRole()));
        employeeRepository.save(employee.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeRole")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Object> removeRole(@RequestBody RoleRequestDTO request){
        Optional<Employee> employee = employeeRepository.findById(request.getEmployeeId());
        if (employee.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        employee.get().getRoles().remove(AccountRoles.valueOf(request.getRole()));
        employeeRepository.save(employee.get());
        return ResponseEntity.ok().build();
    }


}
