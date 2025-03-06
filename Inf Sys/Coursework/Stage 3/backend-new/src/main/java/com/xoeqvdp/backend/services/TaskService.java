package com.xoeqvdp.backend.services;

import com.xoeqvdp.backend.entities.TaskAssignment;
import com.xoeqvdp.backend.entities.TaskDescription;
import com.xoeqvdp.backend.repositories.TaskAssignmentRepoClass;
import com.xoeqvdp.backend.repositories.TaskAssignmentRepository;
import com.xoeqvdp.backend.repositories.TaskDescriptionRepository;
import com.xoeqvdp.backend.repositories.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskDescriptionRepository taskDescriptionRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TaskAssignmentRepoClass taskAssignmentRepoClass;

    public TaskService(TaskDescriptionRepository taskDescriptionRepository, TaskAssignmentRepository taskAssignmentRepository, TasksRepository tasksRepository, TaskAssignmentRepoClass taskAssignmentRepoClass) {
        this.taskDescriptionRepository = taskDescriptionRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.taskAssignmentRepoClass = taskAssignmentRepoClass;
    }

    @Transactional
    public void createTask(String name, String description, List<Long> employeeIds, Long taskId, Long levelId) {
        TaskDescription task = new TaskDescription();
        task.setName(name);
        task.setDescription(description);
        taskDescriptionRepository.save(task);

        Long tdId = task.getId();

        for(Long eId: employeeIds){
            taskAssignmentRepoClass.assignTaskToEmployees(eId, taskId, levelId,tdId);
        }
    }
}