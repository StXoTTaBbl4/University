package com.xoeqvdp.backend.dto;

import com.xoeqvdp.backend.entities.AccountRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@AllArgsConstructor
@Getter
@Setter
public class EmployeeRolesDTO {
    Long employeeId;
    List<AccountRoles> roles;
}
