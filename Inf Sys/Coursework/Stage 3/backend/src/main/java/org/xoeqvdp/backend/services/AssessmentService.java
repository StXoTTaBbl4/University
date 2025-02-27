package org.xoeqvdp.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xoeqvdp.backend.dto.AssessmentItem;
import org.xoeqvdp.backend.entities.*;
import org.xoeqvdp.backend.repositories.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssessmentService {
    private static final Logger logger = LoggerFactory.getLogger(AssessmentService.class);

    private final LevelsRepository levelsRepository;
    private final TasksRepository tasksRepository;
    private final ProductsRepository productsRepository;
    private final EmployeeSkillsRepository employeeSkillsRepository;
    private final EmployeeRepository employeeRepository;

    private final JwtService jwtService;

    public AssessmentService(LevelsRepository levelsRepository, TasksRepository tasksRepository, ProductsRepository productsRepository, EmployeeSkillsRepository employeeSkillsRepository, EmployeeRepository employeeRepository, JwtService jwtService) {
        this.levelsRepository = levelsRepository;
        this.tasksRepository = tasksRepository;
        this.productsRepository = productsRepository;
        this.employeeSkillsRepository = employeeSkillsRepository;
        this.employeeRepository = employeeRepository;
        this.jwtService = jwtService;
    }

    public List<Level> getLevels(){
        return levelsRepository.findAll();
    }

    public String getProductsAsJson(BlockType type){
        List<Product> products = productsRepository.findAllByBlockType(type);
        return getAsJson(products);
    }

    public List<Task> getTasks(BlockType type){
        List<Product> products = productsRepository.findAllByBlockType(type);
        List<Task> tasks = new ArrayList<>();
        for (Product p: products) {
            tasks.addAll(tasksRepository.findTasksByProduct(p));
        }

        return tasks;
    }



    private <T> String getAsJson(List<T> list){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            logger.error("Failed to create JSON string: {}", e.getMessage());
            return null;
        }
    }

    public void saveAssessment(List<AssessmentItem> hw, List<AssessmentItem> sw, List<AssessmentItem> pr, String accessToken) {
        Employee employee = employeeRepository.findByEmail(jwtService.extractEmail(accessToken)).orElse(null);

        if (employee == null) {
            throw new NullPointerException("Пользователь не найден");
        }

        if (!hw.isEmpty()) {
            saveSkills(hw, employee);
        }

        if (!sw.isEmpty()) {
            saveSkills(sw, employee);
        }

        if (!pr.isEmpty()) {
            saveSkills(pr, employee);
        }
    }

    private void saveSkills(List<AssessmentItem> items, Employee employee){
        for(AssessmentItem item: items){
            String[] arr = item.getName().split(":");
            Product product = productsRepository.findByProduct(arr[0]).orElse(null);
            if (product == null){
                logger.warn("process {} not found in DB",arr[0]);
                continue;
            }

            Task task = tasksRepository.findTaskByProductAndTask(product, arr[1]);
            if (task == null){
                logger.warn("processes task {} not found in DB",arr[1]);
                continue;
            }

            Level level = levelsRepository.findByWeight(Long.valueOf(item.getValue())).orElse(null);
            if (level == null){
                logger.warn("level with weight {} not found in DB", item.getValue());
                continue;
            }

            EmployeeSkill skill = new EmployeeSkill(LocalDateTime.now(), level, task, employee);

            EmployeeSkill db_skill = employeeSkillsRepository.findByEmployeeAndTask(employee, task).orElse(null);
            if (db_skill != null){
                if (db_skill.getLevel().getWeight().equals(level.getWeight())) {
                    continue;
                }
                employeeSkillsRepository.updateByEmployeeAndTaskAndLevel(employee, task, level);
                continue;
            }

            employeeSkillsRepository.save(skill);
        }
    }

//    public String getHardwareTasksAsJson(){
//        List<Task> tasks = tasksRepository.findAllByProduct();
//    }

}
