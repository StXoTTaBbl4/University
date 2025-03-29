package org.xoeqvdp.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xoeqvdp.backend.dto.AssessmentItem;
import org.xoeqvdp.backend.dto.AssessmentRequest;
import org.xoeqvdp.backend.entities.BlockType;
import org.xoeqvdp.backend.entities.Level;
import org.xoeqvdp.backend.entities.Task;
import org.xoeqvdp.backend.services.AssessmentService;
import org.xoeqvdp.backend.services.JwtService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/assessment")
public class SelfAssessmentController {

    AssessmentService assessmentService;
    JwtService jwtService;

    public SelfAssessmentController(AssessmentService assessmentService, JwtService jwtService) {
        this.assessmentService = assessmentService;
        this.jwtService = jwtService;
    }

    @GetMapping("/hardware/products")
    public ResponseEntity<Map<String, String>> getHardwareProducts(@RequestHeader(value = "Authorization") String authHeader) {
        Map<String, String> response = new HashMap<>();
        if (!jwtService.validateAccessToken(authHeader.substring(7))){
            response.put("message", "Invalid token");
            return ResponseEntity.badRequest().body(response);
        }

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
        if (!jwtService.validateAccessToken(authHeader.substring(7))){
            return ResponseEntity.badRequest().body(null);
        }
        response.put("levels", assessmentService.getLevels());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit-assessment")
    public ResponseEntity<Map<String, String>> saveAssessment(@RequestHeader(value = "Authorization") String authHeader, @RequestBody AssessmentRequest request){
//        printAssessmentList("HW", request.getHw());
//        printAssessmentList("SW", request.getSw());
//        printAssessmentList("PR", request.getPr());

        assessmentService.saveAssessment(request.getHw(), request.getSw(), request.getPr(), authHeader.substring(7));

        Map<String, String> response = new HashMap<>();
        response.put("message", "Данные получены.");
        return ResponseEntity.ok(response);
    }

    private void printAssessmentList(String category, List<AssessmentItem> items) {
        if (items != null) {
            for (AssessmentItem item : items) {
                System.out.println(category + ": " + item.getName() + " - " + item.getValue());
            }
        }
    }
}
