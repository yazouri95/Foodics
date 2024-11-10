package com.foodics.task.repository.dbo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@Getter
@Setter
public class OrderDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProductDBO> orderProducts = new ArrayList<>();


    public void addOrderProduct(OrderProductDBO orderProductDBO) {
        orderProducts.add(orderProductDBO);
        orderProductDBO.setOrder(this);
    }
}
