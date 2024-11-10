package com.foodics.task.domain.exception;

public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(Long id) {
        super(String.format("Product with ID %d not found.", id));
    }
}
