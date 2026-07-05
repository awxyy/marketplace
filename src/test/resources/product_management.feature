@database-isolate
Feature: Product Management API
  As a registered Seller
  I want to manage products in the marketplace
  So that customers can buy them

  Background:
    Given a registered user exists with login "seller_john" and password "Pass123!" with role "SELLER"
    And the user logs in with login "seller_john" and password "Pass123!"
    And the seller profile is synchronized in the database

  # 1. HAPPY PATH + STRICT HEADERS + RESPONSE DTO VALIDATION + DATA INTEGRITY
  Scenario: Successfully create, read,  update and delete a product (Full Lifecycle)
    When I create a new product with the following details:
      | name        | Mechanical Keyboard   |
      | description | RGB Gaming Keyboard   |
      | price       | 89.99                 |
      | quantity    | 15                    |
    Then the server returns HTTP status 200
    And the response headers contain "Content-Type" with value "application/json"
    And the response body matches the Product DTO schema and values

    # Read-after-write consistency
    When I request the product details by its generated ID
    Then the server returns HTTP status 200
    And the response headers contain "Content-Type" with value "application/json"
    And the retrieved product name is "Mechanical Keyboard"

    When I update product by its generated ID
      | name  |  new name |
      | price | 150       |
    Then the server returns HTTP status 200
    And product changes are persisted

    # Cleanup / Idempotent operation verification
    When I delete this product by its ID
    Then the server returns HTTP status 204
    And the response body is empty

    # Verify resource is truly deleted (Resource not found)
    When I request the product details by its generated ID
    Then the server returns HTTP status 404

  # 2. ROBUST ERROR VALIDATION / SPRING GLOBAL EXCEPTION STRUCTURE
  Scenario Outline: Fail to create product due to validation constraints
    When I try to create a product with name "<name>", price <price>, and quantity <quantity>
    Then the server returns HTTP status 400
    And the response headers contain "Content-Type" with value "application/json"
    And the error response structure matches the validation schema with message "<error_message>"

    Examples:
      | name     | price | quantity | error_message                |
      |          | 50.00 | 10       | Name is required             |
      | Keyboard | 0.00  | 10       | Price must be greater than 0 |
      | Keyboard | -5.00 | 10       | Price must be greater than 0 |
      | Keyboard | 50.00 | -5       | Quantity cannot be negative  |

  # 3. DUPLICATE RESOURCE VALIDATION (Business rule)
  Scenario: Fail to create a duplicate product
    And a product with name "Unique Mug" already exists
    When I create a new product with the following details:
      | name        | Unique Mug |
      | description | Ceramic mug|
      | price       | 10.00      |
      | quantity    | 5          |
    Then the server returns HTTP status 409
    And the error response structure matches the validation schema with message "Product already exists"

  # 4. ADVANCED AUTHENTICATION & AUTHORIZATION (ROLES, INVALID TOKENS)
  Scenario Outline: Access control and token validity verification
    Given the authorization state is altered to "<token_state>"
    When I create a new product with the following details:
      | name        | Secure Item |
      | description | Top Secret  |
      | price       | 10.00       |
      | quantity    | 5           |
    Then the server returns HTTP status <expected_status>

    Examples:
      | token_state      | expected_status |
      | INSUFFICIENT_ROLE| 403             |
      | MISSING          | 401             |
      | INVALID          | 401             |
      | EXPIRED          | 401             |