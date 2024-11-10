package com.foodics.task.repository.dbo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "order_product")
@Getter
@Setter
public class OrderProductDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderDBO order;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductDBO product;
    private int quantity;
}
