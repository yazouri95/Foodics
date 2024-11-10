--liquibase formatted sql

--changeset abdalrahman:1
CREATE TABLE product
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset abdalrahman:2
CREATE TABLE standard_unit
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    code       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


--changeset abdalrahman:3
CREATE TABLE ingredient
(
    id                    BIGSERIAL PRIMARY KEY,
    name                  VARCHAR(255)     NOT NULL,
    initial_amount        DOUBLE PRECISION NOT NULL, -- Initial amount (e.g., 20kg for Beef)
    current_amount        DOUBLE PRECISION NOT NULL, -- Current amount level in grams or kilograms
    low_amount_alert_sent BOOLEAN   DEFAULT FALSE,   -- Prevent duplicate alert notifications
    unit_id               BIGINT           NOT NULL, -- unit in grams or kilograms etc...
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_unit FOREIGN KEY (unit_id) REFERENCES standard_unit (id) ON DELETE CASCADE
);

--changeset abdalrahman:4
CREATE TABLE product_ingredient
(
    id            BIGSERIAL PRIMARY KEY,
    product_id    BIGINT           NOT NULL,
    ingredient_id BIGINT           NOT NULL,
    amount        DOUBLE PRECISION NOT NULL, -- Amount of the ingredient required for the product
    unit_id       BIGINT           NOT NULL, -- unit in grams or kilograms etc...
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE,
    CONSTRAINT fk_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredient (id) ON DELETE CASCADE,
    CONSTRAINT fk_unit_id FOREIGN KEY (unit_id) REFERENCES standard_unit (id) ON DELETE CASCADE
);

--changeset abdalrahman:5
CREATE TABLE orders
(
    id         BIGSERIAL PRIMARY KEY,
    -- Add other necessary fields for Order, such as customer info or order date
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset abdalrahman:6
CREATE TABLE order_product
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT    NOT NULL, -- Quantity of the product ordered
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);