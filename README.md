## Foodics - Problem Statement

In a system that has three main models; Product, Ingredient, and Order.

A Burger (Product) may have several ingredients:
```phpt
- 150g Beef
- 30g Cheese
- 20g Onion
```

The system keeps the stock of each of these ingredients stored in the database.
You can use the following levels for seeding the database:

```phpt
- 20kg Beef 
- 5kg Cheese
- 1kg Onion
```

When a customer makes an order that includes a Burger. The system needs to update the
stock of each of the ingredients so, it reflects the amounts consumed. Also when any of the ingredients stock level reaches 50%, the system should send an
email message to alert the merchant they need to buy more of this ingredient.

### Requirements:

<b>First</b>, Write a controller action that:

1. Accepts the order details from the request payload.
2. Persists the Order in the database.
3. Updates the stock of the ingredients.

<b>Second</b>, ensure that en email is sent once the level for any of the ingredients reach
below 50%. Only a single email should be sent, further consumption of the same
ingredient below 50% shouldn't trigger an email.

- **Endpoint**: `POST /api/v1/orders` 
`payload` looks like this:
```
{
    "products": [
        {
            "product_id": 1,
            "quantity": 2,
        }
    ]
}
```
<hr>

### Prerequisites

- **Java 17**
- **Apache Maven (3.9.5+).**
- **Postgres**
- **Set Postgres properties(url, username, password) in your application.yml**
- **SYSTEM_EMAIL**:  is required. Set this key in your application.yml
- **SYSTEM_EMAIL_PASSWORD**:  is required. Set this key in your application.yml
- **MERCHANT_EMAIL**:  is required. Set this key in your application.yml

## Steps to Run the Application

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yazouri95/foodics.git

2. Running the Application:
    ```
    $ mvn clean install
    $ mvn spring-boot:run
    ```
   The app will start at http://localhost:8080


### Tests

`mvn clean test`
