package com.xoeqvdp.backend.dto;

import com.xoeqvdp.backend.entities.AccountRoles;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    String accessToken;
    Long accessTokenExpiresAt;
    String message;
    List<AccountRoles> roles;
    Long id;
}
