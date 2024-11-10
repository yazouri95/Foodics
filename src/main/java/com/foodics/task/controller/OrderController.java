package com.foodics.task.controller;

import com.foodics.task.domain.model.OrderList;
import com.foodics.task.dto.OrderRequestDTO;
import com.foodics.task.dto.PlaceOrderResponse;
import com.foodics.task.mapper.OrderMapper;
import com.foodics.task.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestBody @Valid OrderRequestDTO orderRequestDTO) {
        OrderList orders = OrderMapper.DTOsToModel(orderRequestDTO);
        orderService.placeOrder(orders);
        return ResponseEntity.ok(new PlaceOrderResponse("Order placed successfully"));
    }
}

