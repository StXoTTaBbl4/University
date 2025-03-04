package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshAccessTokenRequestDTO {
    String email;
}
