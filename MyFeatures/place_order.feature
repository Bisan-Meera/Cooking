Feature: Place an Order

  As a customer
  I want to select meals from the available menu
  So that I can place an order and it gets managed by the kitchen staff

  Background:
    Given the user is logged in as a customer
    And the system has predefined meals

  Scenario: Customer places a single meal order depending on stock
    When the customer selects the following meal:
      | Spring Rolls |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer places a valid meal order
    When the customer selects the following meal:
      | Grilled Salmon |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer places a multiple meal order depending on stock
    When the customer selects the following meals:
      | Chicken Biryani     |
      | Grilled Salmon      |
      | Vegetarian Lasagna  |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer tries to order a meal with low ingredient stock
    When the customer selects the following meal:
      | Grilled Salmon |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer tries to order a non-existent meal
    When the customer selects the following meal:
      | Flying Pizza |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Database error occurs during order creation
    When the customer selects the following meal:
      | Grilled Salmon |
    And a database error is simulated
    Then the system should handle the order according to ingredient stock levels



  Scenario: Customer tries to order a meal with low ingredient stock
    When the customer selects the following meal:
      | Grilled Salmon |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer tries to order a meal with low ingredient stock
    When the customer selects the following meal:
      | Grilled Salmon |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer tries to order a non-existent meal
    When the customer selects the following meal:
      | Flying Pizza |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer places a valid meal order
    When the customer selects the following meal:
      | Grilled Salmon |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer places a multiple meal order depending on stock
    When the customer selects the following meals:
      | Chicken Biryani     |
      | Grilled Salmon      |
      | Vegetarian Lasagna  |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer places a single meal order depending on stock
    When the customer selects the following meal:
      | Spring Rolls |
    Then the system should handle the order according to ingredient stock levels

  Scenario: Customer tries to place an order with no meals
    When the customer selects no meals
    Then the system should handle the order according to ingredient stock levels


  Scenario: Customer views their order history
    When the customer requests their order history
    Then the system should return a list of their past orders

  Scenario: Customer places an order with multiple meals
    Given the system has predefined meals
    When the customer selects the following meals:
      | Vegan Pesto Pasta    |
      | Vegetarian Lasagna  |
    Then an order should be added to the system
    And a task should be created for kitchen management


  Scenario: Customer places an order with a meal containing one ingredient
    Given the system has predefined meals
    When the customer selects the following meal:
      | Vegan Pesto Pasta      |
    Then an order should be added to the system
    And a task should be created for kitchen management

  Scenario: Database error occurs while placing an order
    Given the system has predefined meals
    And a database error is simulated
    When the customer selects the following meals:
      | Vegetarian Lasagna     |
      | Vegan Pesto Pasta      |
    Then the system should handle the order according to ingredient stock levels

