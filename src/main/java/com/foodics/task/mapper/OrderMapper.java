package com.foodics.task.mapper;

import com.foodics.task.domain.model.OrderItem;
import com.foodics.task.domain.model.OrderList;
import com.foodics.task.dto.OrderDTO;
import com.foodics.task.dto.OrderRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

public class OrderMapper {
    private OrderMapper() {
    }

    public static OrderItem DTOToModel(OrderDTO orderDTO) {
        return new OrderItem(orderDTO.getProductId(), orderDTO.getQuantity(), LocalDateTime.now());
    }

    public static OrderList DTOsToModel(OrderRequestDTO orderRequestDTO) {
        List<OrderItem> orders = orderRequestDTO
                .getProducts()
                .stream()
                .map(OrderMapper::DTOToModel)
                .toList();

        return new OrderList(orders);
    }
}
