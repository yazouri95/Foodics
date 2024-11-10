package com.foodics.task.repository;

import com.foodics.task.repository.dbo.IngredientDBO;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<IngredientDBO, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM ingredient i WHERE i.id = :id")
    Optional<IngredientDBO> findByIdForUpdate(Long id);
}

