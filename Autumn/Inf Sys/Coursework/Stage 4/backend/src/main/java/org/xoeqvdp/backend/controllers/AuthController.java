package org.xoeqvdp.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xoeqvdp.backend.dto.SignInRequest;
import org.xoeqvdp.backend.dto.SignUpRequest;
import org.xoeqvdp.backend.services.AuthService;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Authentication", description = "API для авторизации и токенов")
@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }



    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody SignInRequest signInRequest, HttpServletResponse signInResponse) {
        try {
            String refreshToken = authService.validateRefreshToken(signInRequest.getEmail(), signInRequest.getPassword());
            String accessToken = authService.authenticateAndGenerateToken(signInRequest.getEmail(), signInRequest.getPassword());

            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/api/auth/refresh");
            cookie.setMaxAge(7 * 24 * 60 * 60);

            signInResponse.addCookie(cookie);
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ошибка аутентификации: " + e.getMessage());
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Operation(summary = "Регистрация пользователя", description = "Создает нового пользователя в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные/Уже существует такой пользователь")
    })
    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody SignUpRequest signUpRequest) {
        System.out.println(signUpRequest.getEmail()+ "\n" + signUpRequest.getPassword());
        try {
            authService.registerEmployee(signUpRequest.getEmail(), signUpRequest.getName(), signUpRequest.getPassword());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Пользователь создан, выполните вход!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ошибка регистрации: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Operation(summary = "Обновление Access-токена", description = "Получение нового Access-токена по Refresh-токену")
    @ApiResponse(responseCode = "200", description = "Новый токен успешно выдан")
    @PostMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody SignUpRequest signUpRequest){
        return ResponseEntity.ok(null);
    }
}
