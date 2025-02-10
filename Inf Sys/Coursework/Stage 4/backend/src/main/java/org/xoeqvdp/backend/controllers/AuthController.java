package org.xoeqvdp.backend.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xoeqvdp.backend.entities.Employee;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> loginUser(@RequestBody Employee employee) {
        System.out.println(employee.getLogin()+ "\n" + employee.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Данные успешно получены!");
        return response;
    }
}
