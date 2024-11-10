package com.foodics.task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {
    @NotNull
    @JsonProperty("product_id")
    private Long productId;
    @NotNull
    private Integer quantity;
}
