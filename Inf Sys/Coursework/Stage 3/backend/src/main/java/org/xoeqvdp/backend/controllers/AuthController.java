package org.xoeqvdp.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xoeqvdp.backend.dto.SignInRequest;
import org.xoeqvdp.backend.dto.SignUpRequest;
import org.xoeqvdp.backend.services.AuthService;
import org.xoeqvdp.backend.services.JwtService;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Authentication", description = "API для авторизации и токенов")
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }



    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody SignInRequest signInRequest, HttpServletResponse signInResponse) {
        try {
            String refreshToken = authService.refreshOrUpdateRefreshToken(signInRequest.getEmail(), signInRequest.getPassword());
            String accessToken = authService.authenticateAndGenerateToken(signInRequest.getEmail(), signInRequest.getPassword());

            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); //TODO потом вернуть на true
            cookie.setPath("api/auth/refresh");
            cookie.setMaxAge(7 * 24 * 60 * 60);

            signInResponse.addCookie(cookie);
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("expiresAt", String.valueOf(jwtService.getExpirationDate(accessToken).toEpochMilli()));
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
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refresh_token")) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
            if (refreshToken != null && authService.validateRefreshToken(refreshToken)) {
                String token = authService.generateAccessToken(refreshToken);
                Map<String, String> response = new HashMap<>();
                response.put("accessToken", token);
                response.put("expiresAt", String.valueOf(jwtService.getExpirationDate(token).toEpochMilli()));
                return ResponseEntity.ok(response);
            }
            logger.warn("Refresh gone wrong, lets check: refreshToken = {}\n validation {}", refreshToken, authService.validateRefreshToken(refreshToken));
            return ResponseEntity.status(440).body("rtexp");
        }catch (RuntimeException e){
            logger.info("Failed to update access token: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }
}
