package com.xoeqvdp.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks_description")
@Getter
@Setter
public class TaskDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskAssignment> assignments = new ArrayList<>();

    // Геттеры и сеттеры
}
