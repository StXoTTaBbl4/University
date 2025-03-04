package com.xoeqvdp.backend.controllers;

import com.xoeqvdp.backend.dto.*;
import com.xoeqvdp.backend.entities.AccountRoles;
import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.repositories.EmployeeRepository;
import com.xoeqvdp.backend.services.JwtService;
import com.xoeqvdp.backend.services.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(EmployeeRepository employeeRepository, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.employeeRepository = employeeRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Вход на сайт", description = "Отправка почты и пароля для доступа на сайт")
    @ApiResponse(responseCode = "200", description = "Вход успешен")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request, HttpServletResponse response){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        Cookie cookie = new Cookie("refresh_token", jwtService.generateRefreshToken(employee));
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //TODO потом вернуть на true
        cookie.setPath("api/auth/refresh");
        cookie.setMaxAge(jwtService.getRefreshTokenExpirationPeriod());
        response.addCookie(cookie);

        String accessToken = jwtService.generateAccessToken(employee);
        Long accessTokenExpiresAt = Instant.now().toEpochMilli() + jwtService.getAccessTokenExpirationPeriod();

        return ResponseEntity.ok(new LoginResponseDTO(accessToken, accessTokenExpiresAt, ""));
    }

    @Operation(summary = "Регистрация пользователя", description = "Отправка почты, ФИО и пароля на сервер для создания пользователя(по дефолту у всех роль EMPLOYEE)")
    @ApiResponse(responseCode = "200", description = "Пользователь создан")
    @PostMapping("/registration")
    public RegistrationResponseDTO registration(@RequestBody RegistrationRequestDTO request){
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        List<AccountRoles> roles = new ArrayList<>();
        roles.add(AccountRoles.EMPLOYEE);
        employee.setRoles(roles);

        employeeRepository.save(employee);

        return new RegistrationResponseDTO("Пользователь создан, выполните вход!");
    }


    @Operation(summary = "Обновление Access-токена", description = "Получение нового Access-токена по Refresh-токену")
    @ApiResponse(responseCode = "200", description = "Новый токен успешно выдан")
    @ApiResponse(responseCode = "401", description = "Либо Refresh-токен просрочен/отозван, либо не сошлись email из токена и переданный.")
    @PostMapping("/refresh")
    public ResponseEntity<RefreshAccessTokenResponseDTO> refreshAccessToken(@CookieValue(name = "refresh_token", required = false) String refreshToken, RefreshAccessTokenRequestDTO request) {
        Optional<Employee> employee = employeeRepository.findByEmail(request.getEmail());
        Boolean isTokenRevoked= refreshTokenService.isRefreshTokenRevoked(refreshToken);
        if (employee.isPresent() && jwtService.validateToken(refreshToken, employee.get()) && isTokenRevoked != null && !isTokenRevoked){
             return ResponseEntity.ok(new RefreshAccessTokenResponseDTO(jwtService.generateAccessToken(employee.get()), ""));
        }
        return ResponseEntity.status(401).body(new RefreshAccessTokenResponseDTO(null, "Refresh token expired||Wrong email"));
    }
}
