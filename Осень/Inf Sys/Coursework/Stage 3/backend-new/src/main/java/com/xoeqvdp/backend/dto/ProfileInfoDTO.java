package com.xoeqvdp.backend.dto;

import com.xoeqvdp.backend.entities.AccountRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class ProfileInfoDTO {
    String name;
    String email;
    Long id;
    List<AccountRoles> roles;
}
