package com.foodics.task.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Data
public class OrderItem {
    private Long productId;
    private Integer quantity;
    private LocalDateTime orderTime;
}
