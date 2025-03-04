package com.xoeqvdp.backend.controllers;

import com.xoeqvdp.backend.dto.AssessmentItemDTO;
import com.xoeqvdp.backend.dto.AssessmentRequestDTO;
import com.xoeqvdp.backend.entities.BlockType;
import com.xoeqvdp.backend.entities.Level;
import com.xoeqvdp.backend.entities.Task;
import com.xoeqvdp.backend.services.AssessmentService;
import com.xoeqvdp.backend.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/assessment")
public class AssessmentController {
    AssessmentService assessmentService;
    JwtService jwtService;

    public AssessmentController(AssessmentService assessmentService, JwtService jwtService) {
        this.assessmentService = assessmentService;
        this.jwtService = jwtService;
    }

    @GetMapping("/hardware/products")
    public ResponseEntity<Map<String, String>> getHardwareProducts(@RequestHeader(value = "Authorization") String authHeader) {
        Map<String, String> response = new HashMap<>();

        response.put("products", assessmentService.getProductsAsJson(BlockType.Hardware));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hardware/tasks")
    public ResponseEntity<Map<String, List<Task>>> getHardwareTasks() {
        Map<String, List<Task>> response = new HashMap<>();
        response.put("tasks", assessmentService.getTasks(BlockType.Hardware));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/software/tasks")
    public ResponseEntity<Map<String, List<Task>>> getSoftwareTasks() {
        Map<String, List<Task>> response = new HashMap<>();
        response.put("tasks", assessmentService.getTasks(BlockType.Software));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/processes/tasks")
    public ResponseEntity<Map<String, List<Task>>> getProcessesTasks() {
        Map<String, List<Task>> response = new HashMap<>();
        response.put("tasks", assessmentService.getTasks(BlockType.Processes));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/levels")
    public ResponseEntity<Map<String, List<Level>>> getLevelsList(@RequestHeader(value = "Authorization") String authHeader) {
        Map<String, List<Level>> response = new HashMap<>();

        response.put("levels", assessmentService.getLevels());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit-assessment")
    public ResponseEntity<Map<String, String>> saveAssessment(@RequestHeader(value = "Authorization") String authHeader, @RequestBody AssessmentRequestDTO request){
        assessmentService.saveAssessment(request.getHw(), request.getSw(), request.getPr(), authHeader.substring(7));

        Map<String, String> response = new HashMap<>();
        response.put("message", "Данные получены.");
        return ResponseEntity.ok(response);
    }

    private void printAssessmentList(String category, List<AssessmentItemDTO> items) {
        if (items != null) {
            for (AssessmentItemDTO item : items) {
                System.out.println(category + ": " + item.getName() + " - " + item.getValue());
            }
        }
    }
}
