package org.xoeqvdp.backend.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xoeqvdp.backend.entities.AssignedRoles;
import org.xoeqvdp.backend.entities.Employee;
import org.xoeqvdp.backend.entities.Roles;
import org.xoeqvdp.backend.repositories.AssignedRolesRepository;
import org.xoeqvdp.backend.repositories.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;
    private final AssignedRolesRepository assignedRolesRepository;

    public AuthService(JwtService jwtService, EmployeeRepository employeeRepository, AssignedRolesRepository assignedRolesRepository) {
        this.jwtService = jwtService;
        this.employeeRepository = employeeRepository;
        this.assignedRolesRepository = assignedRolesRepository;
    }

    public String authenticateAndGenerateToken(String email, String password) {
        Employee employee = employeeRepository.findByEmail(email).orElse(null);

        if (employee == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        if (!encodePassword(password).equals(employee.getPassword())) {
            throw new RuntimeException("Проверьте правильность пароля");
        }

        List<String> roles = assignedRolesRepository.findRolesByEmployeeID(employee.getId());

        return jwtService.generateToken(employee.getId(), email, roles);

    }

    public void registerEmployee(String email,String name, String password){
        if (employeeRepository.existsByEmail(email)) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setName(name);
        employee.setPassword(encodePassword(password));
        employeeRepository.save(employee);

        AssignedRoles assignedRoles = new AssignedRoles();
        assignedRoles.setEmployee(employee);
        assignedRoles.setRole(Roles.EMPLOYEE);
        assignedRolesRepository.save(assignedRoles);

    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
