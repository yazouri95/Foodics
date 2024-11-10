package com.foodics.task.repository;

import com.foodics.task.repository.dbo.OrderDBO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderDBO, Long> { }
