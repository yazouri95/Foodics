package com.foodics.task.service;

import com.foodics.task.domain.exception.DomainException;
import com.foodics.task.domain.exception.IngredientNotFoundException;
import com.foodics.task.domain.exception.InsufficientStockException;
import com.foodics.task.domain.exception.ProductNotFoundException;
import com.foodics.task.domain.model.EmailMessage;
import com.foodics.task.domain.model.OrderItem;
import com.foodics.task.domain.model.OrderList;
import com.foodics.task.repository.IngredientRepository;
import com.foodics.task.repository.OrderRepository;
import com.foodics.task.repository.ProductRepository;
import com.foodics.task.repository.Unit;
import com.foodics.task.repository.dbo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final EmailService emailService;

    @Value("${merchant.ingredient-low-stock-threshold-percentage}")
    private double lowStockThresholdPercentage = 0.5; // 50%

    @Value("${spring.mail.username}")
    private String sender;
    @Value("${merchant.email:}")
    private String merchantEmail;


    @Transactional
    public void placeOrder(OrderList orderList) {
        OrderDBO order = new OrderDBO();
        orderRepository.save(order);

        for (OrderItem product : orderList.getProducts()) {
            Long productId = product.getProductId();
            int quantity = product.getQuantity();

            ProductDBO productDBO = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

            OrderProductDBO orderProductDBO = new OrderProductDBO();
            orderProductDBO.setOrder(order);
            orderProductDBO.setProduct(productDBO);
            orderProductDBO.setQuantity(quantity);
            order.addOrderProduct(orderProductDBO);

            updateIngredientStock(productDBO, quantity);
        }

        orderRepository.save(order);
    }

    private void updateIngredientStock(ProductDBO productDBO, int quantity) {
        List<ProductIngredientDBO> productIngredients = productDBO.getIngredients();

        for (ProductIngredientDBO productIngredientDBO : productIngredients) {
            long ingredientId = productIngredientDBO.getIngredient().getId();

            IngredientDBO ingredientDBO = ingredientRepository
                    .findByIdForUpdate(ingredientId)
                    .orElseThrow(() -> new IngredientNotFoundException(ingredientId));

            Unit productIngredientUnit = Unit.resolve(productIngredientDBO.getUnit().getCode());
            Unit currentIngredientUnit = Unit.resolve(ingredientDBO.getUnit().getCode());

            double totalProductGramsRequired = productIngredientUnit.toGrams(productIngredientDBO.getAmount()) * quantity;
            double currentProductIngredientAmountGrams = currentIngredientUnit.toGrams(ingredientDBO.getCurrentAmount());

            if (currentProductIngredientAmountGrams < totalProductGramsRequired) {
                throw new InsufficientStockException("Insufficient stock for ingredient: " + ingredientDBO.getName());
            }

            double currentIngredientAmount = currentIngredientUnit.toOrigin(currentProductIngredientAmountGrams - totalProductGramsRequired);
            ingredientDBO.setCurrentAmount(currentIngredientAmount);

            if (shouldSendAlert(ingredientDBO)) {
                try {
                    sendAlert(ingredientDBO);
                    ingredientDBO.setLowAmountAlertSent(true);
                } catch (Exception ignored) {
                }
            }

            ingredientRepository.save(ingredientDBO);
        }
    }

    private boolean shouldSendAlert(IngredientDBO ingredientDBO) {
        Unit ingredientUnit = Unit.resolve(ingredientDBO.getUnit().getCode());
        double currentStockLevel = ingredientUnit.toGrams(ingredientDBO.getCurrentAmount());
        double threshold = ingredientUnit.toGrams(ingredientDBO.getInitialAmount()) * lowStockThresholdPercentage;

        return currentStockLevel <= threshold && !ingredientDBO.isLowAmountAlertSent();
    }

    private void sendAlert(IngredientDBO ingredientDBO) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setFrom(sender);
        emailMessage.setTo(merchantEmail);
        emailMessage.setSubject("Low Stock Alert: " + ingredientDBO.getName());
        emailMessage.setText("The stock level of " + ingredientDBO.getName() + " is below 50%. Please replenish it soon.");
        try {
            log.info("send a simple mail without attachment file");
            emailService.send(emailMessage);
        } catch (Exception exception) {
            log.error("error while sending the mail");
            throw new DomainException("error while sending the mail : "
                    + exception.getMessage());
        }
    }
}

