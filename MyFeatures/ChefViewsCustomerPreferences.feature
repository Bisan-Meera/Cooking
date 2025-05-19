Feature: Chef views customer dietary preferences

  As a chef,
  I want to view the dietary preferences and allergies of customers with active orders,
  So that I can customize meals accordingly and avoid harmful ingredients.

  Scenario: Chef views preferences for a customer with an order
    Given a customer has placed an order
    And the customer has "gluten-free" preference and is allergic to "dairy"
    When the chef opens the order details
    Then the customer's preferences and allergies should be visible

  Scenario: Chef views preferences for multiple customers
    Given multiple customers have placed orders
    When the chef opens the order list
    Then each order should display the corresponding customer's dietary preferences and allergies

  Scenario: Chef handles order with no preferences
    Given a customer has placed an order without saving any preferences
    When the chef opens the order details
    Then the chef should see the message "No preferences specified"

  Scenario: Chef encounters a database error while loading preferences
    Given a special customer ID triggers DB failure
    When the chef opens the order details
    Then the chef should see the message "Error loading preferences"
