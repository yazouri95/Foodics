package com.foodics.task.service;

import com.foodics.task.domain.exception.IngredientNotFoundException;
import com.foodics.task.domain.exception.InsufficientStockException;
import com.foodics.task.domain.exception.ProductNotFoundException;
import com.foodics.task.domain.model.OrderItem;
import com.foodics.task.domain.model.OrderList;
import com.foodics.task.repository.IngredientRepository;
import com.foodics.task.repository.OrderRepository;
import com.foodics.task.repository.ProductRepository;
import com.foodics.task.repository.dbo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderService orderService;

    private ProductDBO productDBO;
    private IngredientDBO beef;
    private IngredientDBO cheese;
    private IngredientDBO onion;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productDBO = new ProductDBO();
        productDBO.setId(1L);
        productDBO.setName("Burger");

        beef = new IngredientDBO();
        beef.setId(1L);
        beef.setName("Beef");
        beef.setInitialAmount(1.0);
        beef.setCurrentAmount(1.0);
        StandardUnitDBO beefUnit = new StandardUnitDBO();
        beefUnit.setId(1L);
        beefUnit.setName("Kilograms");
        beefUnit.setCode("kg");
        beef.setUnit(beefUnit);

        cheese = new IngredientDBO();
        cheese.setId(2L);
        cheese.setName("Cheese");
        cheese.setInitialAmount(5000.0);
        cheese.setCurrentAmount(5000.0);
        StandardUnitDBO cheeseUnit = new StandardUnitDBO();
        cheeseUnit.setId(2L);
        cheeseUnit.setName("Grams");
        cheeseUnit.setCode("g");
        cheese.setUnit(cheeseUnit);

        onion = new IngredientDBO();
        onion.setId(3L);
        onion.setName("Onion");
        onion.setInitialAmount(2.0);
        onion.setCurrentAmount(2.0);
        StandardUnitDBO onionUnit = new StandardUnitDBO();
        onionUnit.setId(3L);
        onionUnit.setName("kilograms");
        onionUnit.setCode("kg");
        onion.setUnit(onionUnit);

        // Mock product ingredient relationships
        ProductIngredientDBO beefIngredient = new ProductIngredientDBO();
        beefIngredient.setId(1L);
        beefIngredient.setProduct(productDBO);
        beefIngredient.setIngredient(beef);
        beefIngredient.setAmount(150); // 150g of Beef per Burger
        StandardUnitDBO beefIngredientUnit = new StandardUnitDBO();
        beefIngredientUnit.setId(1L);
        beefIngredientUnit.setName("grams");
        beefIngredientUnit.setCode("g");
        beefIngredient.setUnit(beefIngredientUnit);


        ProductIngredientDBO cheeseIngredient = new ProductIngredientDBO();
        cheeseIngredient.setId(2L);
        cheeseIngredient.setProduct(productDBO);
        cheeseIngredient.setIngredient(cheese);
        cheeseIngredient.setAmount(30); // 30g of Cheese per Burger
        StandardUnitDBO cheeseIngredientUnit = new StandardUnitDBO();
        cheeseIngredientUnit.setId(1L);
        cheeseIngredientUnit.setName("grams");
        cheeseIngredientUnit.setCode("g");
        cheeseIngredient.setUnit(cheeseIngredientUnit);


        ProductIngredientDBO onionIngredient = new ProductIngredientDBO();
        onionIngredient.setId(3L);
        onionIngredient.setProduct(productDBO);
        onionIngredient.setIngredient(onion);
        onionIngredient.setAmount(20); // 20g of Onion per Burger
        StandardUnitDBO onionIngredientUnit = new StandardUnitDBO();
        onionIngredientUnit.setId(1L);
        onionIngredientUnit.setName("grams");
        onionIngredientUnit.setCode("g");
        onionIngredient.setUnit(onionIngredientUnit);


        productDBO.setIngredients(List.of(beefIngredient, cheeseIngredient, onionIngredient));

    }

    @Test
    void testPlaceOrderSuccessfully() {
        OrderItem orderItem = new OrderItem(1L, 2, LocalDateTime.now());
        OrderList orderList = new OrderList(List.of(orderItem));

        when(productRepository.findById(1L)).thenReturn(Optional.of(productDBO));
        when(ingredientRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(beef));
        when(ingredientRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(cheese));
        when(ingredientRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(onion));

        orderService.placeOrder(orderList);

        verify(orderRepository, times(2)).save(any(OrderDBO.class));
        verify(ingredientRepository, times(3)).save(any(IngredientDBO.class));
    }

    @Test
    void testInsufficientStockThrowsException() {
        // Modify current amount for a test where beef amount is insufficient
        beef.setCurrentAmount(0.2);

        OrderItem orderItem = new OrderItem(1L, 2, LocalDateTime.now()); // Order 2 Burgers
        OrderList orderList = new OrderList(List.of(orderItem));

        when(productRepository.findById(1L)).thenReturn(Optional.of(productDBO));
        when(ingredientRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(beef));
        when(ingredientRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(cheese));
        when(ingredientRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(onion));

        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(orderList));
        assertEquals("Insufficient stock for ingredient: Beef", exception.getMessage());
    }

    @Test
    void testLowStockAlertSentOnce() {
        // Setup stock levels so that after an order, stock goes below 50%
        beef.setCurrentAmount(2.0); // 2kg left
        beef.setInitialAmount(20.0); // Initial stock of 20kg

        OrderItem orderItem = new OrderItem(1L, 2, LocalDateTime.now());// Order 2 Burgers
        OrderList orderList = new OrderList(List.of(orderItem));

        when(productRepository.findById(1L)).thenReturn(Optional.of(productDBO));
        when(ingredientRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(beef));
        when(ingredientRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(cheese));
        when(ingredientRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(onion));

        orderService.placeOrder(orderList);

        verify(emailService, times(1)).send(any()); // Email alert should be sent once
        assertTrue(beef.isLowAmountAlertSent());
    }

    @Test
    void testLowStockAlertNotSentTwice() {
        beef.setCurrentAmount(1.0); // 1kg left
        beef.setInitialAmount(2.0); // Initial stock of 20kg

        OrderItem orderItem = new OrderItem(1L, 2, LocalDateTime.now());  // Order 2 Burgers
        OrderList orderList = new OrderList(List.of(orderItem));

        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(productDBO));
        when(ingredientRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(beef));
        when(ingredientRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(cheese));
        when(ingredientRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(onion));

        orderService.placeOrder(orderList);
        verify(emailService, times(1)).send(any());  // Alert sent once

        orderService.placeOrder(orderList);  // Trying to place another order

        verify(emailService, times(1)).send(any());  // Email alert should not be sent again
    }

    @Test
    void testProductNotFoundThrowsException() {
        OrderItem orderItem = new OrderItem(999L, 2, LocalDateTime.now());  // Invalid product ID
        OrderList orderList = new OrderList(List.of(orderItem));

        when(productRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> orderService.placeOrder(orderList));
        assertEquals("Product with ID 999 not found.", exception.getMessage());
    }

    @Test
    void testIngredientNotFoundThrowsException() {
        OrderItem orderItem = new OrderItem(1L, 2, LocalDateTime.now());  // Valid product
        OrderList orderList = new OrderList(List.of(orderItem));

        ProductIngredientDBO invalidIngredient = new ProductIngredientDBO();
        IngredientDBO invalidIngredientDBO = new IngredientDBO();
        invalidIngredientDBO.setId(10L);
        invalidIngredient.setIngredient(invalidIngredientDBO); // Invalid ingredient

        productDBO.setIngredients(List.of(invalidIngredient));

        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(productDBO));
        when(ingredientRepository.findByIdForUpdate(anyLong())).thenReturn(java.util.Optional.empty());

        IngredientNotFoundException exception = assertThrows(IngredientNotFoundException.class, () -> orderService.placeOrder(orderList));
        assertEquals("Ingredient with ID 10 not found.", exception.getMessage());
    }
}