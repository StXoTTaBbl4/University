package org.xoeqvdp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xoeqvdp.backend.entities.BlockType;
import org.xoeqvdp.backend.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByBlockType(BlockType type);

    Optional<Product> findByProduct(String product);
}
