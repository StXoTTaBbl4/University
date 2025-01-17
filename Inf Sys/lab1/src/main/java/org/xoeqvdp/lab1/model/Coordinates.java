package org.xoeqvdp.lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coordinates")
@Getter
@Setter
    public class Coordinates {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        @Min(-694)
        private Long x;

        @Column(nullable = false)
        private Long y;
    }
