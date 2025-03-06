package com.xoeqvdp.backend.controllers;

import com.xoeqvdp.backend.dto.TaskRequestDTO;
import com.xoeqvdp.backend.entities.TaskDescription;
import com.xoeqvdp.backend.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<TaskDescription> createTask(@RequestBody TaskRequestDTO request) {
        taskService.createTask(request.getName(), request.getDescription(), request.getEmployeeIds(), request.getTaskId(), request.getLevelId());
        return ResponseEntity.ok().build();
    }
}

