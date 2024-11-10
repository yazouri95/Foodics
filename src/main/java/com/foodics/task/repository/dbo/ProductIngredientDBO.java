package com.foodics.task.repository.dbo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "product_ingredient")
@Getter
@Setter
public class ProductIngredientDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductDBO product;
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientDBO ingredient;
    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private StandardUnitDBO unit;
    private double amount; // Amount of the ingredient required for this product
}
