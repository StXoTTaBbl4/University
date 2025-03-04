package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.BlockType;
import com.xoeqvdp.backend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByBlockType(BlockType type);

    Optional<Product> findByProduct(String product);
}
