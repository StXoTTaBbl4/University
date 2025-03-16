package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xoeqvdp.backend.entities.Product;
import org.xoeqvdp.backend.entities.Task;

import java.util.List;

public interface TasksRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProduct(Product product);

    List<Task> findTasksByProduct(Product product);

    Task findTaskByProductAndTask(Product product, String task);
}
