INSERT INTO product (name)
VALUES ('Burger');


INSERT INTO standard_unit (name, code)
VALUES ('grams', 'g'),
       ('kilograms', 'kg');

INSERT INTO ingredient (name, initial_amount, current_amount, unit_id)
VALUES ('Beef', 20000, 20000, 1),
       ('Cheese', 5000, 5000, 1),
       ('Onion', 1000, 1000, 1);

INSERT INTO product_ingredient (product_id, ingredient_id, amount, unit_id)
VALUES (1, 1, 150, 1), -- Burger requires 150g Beef
       (1, 2, 30, 1),  -- Burger requires 30g Cheese
       (1, 3, 20, 1); -- Burger requires 20g Onion
