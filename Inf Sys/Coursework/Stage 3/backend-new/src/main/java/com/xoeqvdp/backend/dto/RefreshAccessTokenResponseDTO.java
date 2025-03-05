package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshAccessTokenResponseDTO {
    String accessToken;
    Long expiresAt;
    String message;
}
