package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@AllArgsConstructor
public class RefreshTokenListDTO {
    Long employee_id;
    Instant expiresAt;
    Boolean revoked;
}
