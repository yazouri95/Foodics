package com.foodics.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodics.task.dto.OrderDTO;
import com.foodics.task.dto.OrderRequestDTO;
import com.foodics.task.service.OrderService;
import com.foodics.task.domain.exception.ProductNotFoundException;
import com.foodics.task.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void placeOrder_Success() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1L, 2);
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(Collections.singletonList(orderDTO));

        // Mock the service call
        doNothing().when(orderService).placeOrder(any());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order placed successfully"));
    }

    @Test
    void placeOrder_ProductNotFoundException() throws Exception {

        OrderDTO orderDTO = new OrderDTO(1L, 2);
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(Collections.singletonList(orderDTO));

        // Arrange
        doThrow(new ProductNotFoundException(1L)).when(orderService).placeOrder(any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Product with ID 1 not found."));
    }

    @Test
    void placeOrder_InsufficientStockException() throws Exception {

        OrderDTO orderDTO = new OrderDTO(1L, 2);
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(Collections.singletonList(orderDTO));

        // Arrange
        doThrow(new InsufficientStockException("Insufficient stock")).when(orderService).placeOrder(any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Insufficient stock"));
    }
}
