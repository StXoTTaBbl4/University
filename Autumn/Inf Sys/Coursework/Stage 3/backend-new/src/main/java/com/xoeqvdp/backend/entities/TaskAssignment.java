package com.xoeqvdp.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tasks_assignment")
@Getter
@Setter
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private TaskDescription task;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
}
