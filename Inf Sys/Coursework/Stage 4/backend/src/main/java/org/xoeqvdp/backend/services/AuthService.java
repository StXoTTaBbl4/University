package org.xoeqvdp.backend.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xoeqvdp.backend.entities.AssignedRoles;
import org.xoeqvdp.backend.entities.Employee;
import org.xoeqvdp.backend.entities.RefreshToken;
import org.xoeqvdp.backend.entities.Roles;
import org.xoeqvdp.backend.repositories.AssignedRolesRepository;
import org.xoeqvdp.backend.repositories.EmployeeRepository;
import org.xoeqvdp.backend.repositories.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;
    private final AssignedRolesRepository assignedRolesRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(JwtService jwtService, EmployeeRepository employeeRepository, AssignedRolesRepository assignedRolesRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.employeeRepository = employeeRepository;
        this.assignedRolesRepository = assignedRolesRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String authenticateAndGenerateToken(String email, String password) {
        Employee employee = employeeRepository.findByEmail(email).orElse(null);

        if (employee == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            throw new RuntimeException("Проверьте правильность пароля");
        }

        List<String> roles = assignedRolesRepository.findRolesByEmployeeID(employee);

        return jwtService.generateAccessToken(employee, roles);

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

    public String validateRefreshToken(String email, String password) {
        Employee employee = employeeRepository.findByEmail(email).orElse(null);

        if (employee == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByEmployee(employee).orElse(null);

        if (refreshToken != null && refreshToken.getExpiresAt().isAfter(Instant.now())) {
            return refreshToken.getToken();
        }

        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setEmployee(employee);
        }

        refreshToken.setToken(jwtService.generateRefreshToken(employee));
        refreshToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(Instant.now());

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
