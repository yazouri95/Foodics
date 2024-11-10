package com.foodics.task.domain.exception;

public class IngredientNotFoundException extends DomainException {

    public IngredientNotFoundException(long id) {
        super(String.format("Ingredient with ID %d not found.", id));
    }
}
