Feature: Inventory Management System
  The system should manage ingredient stock levels and alert staff when items are low.

  Background:
    Given the following ingredients exist:
      | ingredient_id | name     | stock_quantity | unit | threshold |
      | 1             | Chicken  | 5.00           | kg   | 1.00      |
      | 9             | Onion    | 1.00           | kg   | 2.00      |

  Scenario: Stock update after meal preparation
    Given 2kg of chicken was used for an order
    When the meal is marked as prepared
    Then the stock of chicken should reduce by 2kg
    And the new stock should be 3.00kg

  Scenario: Low stock alert generation
    Given only 1kg of onions is left and the threshold is 2kg
    When another meal uses 0.5kg of onions
    Then the system should trigger a low-stock alert for "Onion"

  Scenario: View stock levels
    Given kitchen staff opens the inventory page
    When the page loads
    Then it should list each ingredient with its current quantity and threshold
    And highlight any ingredients below threshold
