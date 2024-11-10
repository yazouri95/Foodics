package com.foodics.task.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequestDTO {
    @NotEmpty
    private List<OrderDTO> products;
}