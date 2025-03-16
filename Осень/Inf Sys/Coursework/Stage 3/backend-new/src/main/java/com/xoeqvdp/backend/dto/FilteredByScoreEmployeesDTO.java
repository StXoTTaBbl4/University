package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilteredByScoreEmployeesDTO {
    private Long employeeId;
    private String name;
    private String email;
    private Integer hardwarePoints;
    private Integer softwarePoints;
    private Integer processesPoints;
}
