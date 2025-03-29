package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CertificatesInfoResponseDTO {
    String name;
    Long id;
    String category;
    String subCategory;
    String employee_name;
}
