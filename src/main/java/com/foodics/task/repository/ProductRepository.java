package com.foodics.task.repository;

import com.foodics.task.repository.dbo.ProductDBO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductDBO, Long> {
}
