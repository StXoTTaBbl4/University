package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchEmployeeDTO {
    Long id;
    String name;
    String email;
}
