package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class RoleRequestDTO {
    String role;
    Long employeeId;
}
