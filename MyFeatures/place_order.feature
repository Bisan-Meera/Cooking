Feature: Place an Order

  As a customer
  I want to select meals from the available menu
  So that I can place an order and it gets managed by the kitchen staff

  Background:
    Given the user is logged in as a customer
    And the system has predefined meals

  Scenario: Customer places a single meal order
    When the customer selects the following meal:
      | Chicken Biryani |
    Then an order should be added to the system
    And a task should be created for kitchen management

  Scenario: Customer places a multiple meal order
    When the customer selects the following meals:
      | Chicken Biryani     |
      | Grilled Salmon      |
      | Vegetarian Lasagna  |
    Then an order should be added to the system
    And a task should be created for kitchen management
