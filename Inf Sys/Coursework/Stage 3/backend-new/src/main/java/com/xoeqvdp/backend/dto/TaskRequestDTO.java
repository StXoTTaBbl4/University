package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class TaskRequestDTO {
    private String name;
    private String description;
    private List<Long> employeeIds;
    private Long taskId;
    private Long levelId;
}

