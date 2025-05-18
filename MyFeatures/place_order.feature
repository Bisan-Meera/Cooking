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
