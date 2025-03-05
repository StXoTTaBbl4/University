package com.xoeqvdp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class LeaderSearchFiltersDTO {
    Integer hw;
    Integer sw;
    Integer pr;
}
