Feature: Custom Meal Creator
  Customers should be able to build custom meals using available ingredients, with checks for availability and allergies.

  Background:
    Given the Ingredients table has current stock levels
    And the user is logged in as a customer

  Scenario: Create custom meal with available ingredients
    Given a customer selects chicken, rice, and broccoli
    When they submit the custom meal request
    Then the system should accept and save the customized order
    And deduct the used ingredient quantities from stock

  Scenario: Ingredient unavailable
    Given the ingredient "avocado" is currently out of stock
    When a customer tries to add "avocado" to their custom meal
    Then the system should notify "Avocado is unavailable"
    And suggest an available alternative ingredient

  Scenario: Ingredient conflicts with allergy
    Given the customer is allergic to dairy
    When they try to add "cheese" to their custom meal
    Then the system should block the selection
    And show a warning "Ingredient conflicts with your allergy: Dairy"

  Scenario Outline: Submit empty custom meal
    Given a customer is on the custom meal creator page
    When they submit the form without selecting any ingredients
    Then the system should show "At least one ingredient must be selected"

    Examples:
      | UserID |
      | 1      |
      | 2      |

  Scenario Outline: Database error while saving custom meal
    Given a customer selects ingredients for a custom meal
    When a database error occurs during save
    Then the system should display "Failed to save custom meal due to system error"

    Examples:
      | UserID |
      | 1      |
      | 2      |
