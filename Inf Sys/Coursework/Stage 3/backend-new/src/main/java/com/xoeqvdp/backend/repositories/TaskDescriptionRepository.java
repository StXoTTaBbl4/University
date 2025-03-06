package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.TaskDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDescriptionRepository extends JpaRepository<TaskDescription, Long> {
}
