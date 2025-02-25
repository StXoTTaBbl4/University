package org.xoeqvdp.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xoeqvdp.backend.dto.SignInRequest;
import org.xoeqvdp.backend.dto.SignUpRequest;
import org.xoeqvdp.backend.entities.Employee;
import org.xoeqvdp.backend.services.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody SignInRequest signInRequest) {
        System.out.println(signInRequest.getEmail()+ "\n" + signInRequest.getPassword());
        try {
            String token = authService.authenticateAndGenerateToken(signInRequest.getEmail(), signInRequest.getPassword());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка аутентификации: " + e.getMessage());
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<String> createUser(@RequestBody SignUpRequest signUpRequest) {
        System.out.println(signUpRequest.getEmail()+ "\n" + signUpRequest.getPassword());
        try {
            authService.registerEmployee(signUpRequest.getEmail(), signUpRequest.getName(), signUpRequest.getPassword());
            return ResponseEntity.ok("Пользователь создан, выполните вход!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка регистрации: " + e.getMessage());
        }
    }
}
