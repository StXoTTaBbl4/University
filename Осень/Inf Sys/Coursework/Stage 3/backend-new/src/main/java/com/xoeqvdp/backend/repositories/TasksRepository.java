package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.Product;
import com.xoeqvdp.backend.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface TasksRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProduct(Product product);

    List<Task> findTasksByProduct(Product product);

    Task findTaskByProductAndTask(Product product, String task);
}
