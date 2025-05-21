Feature: Custom Meal Creation and Ingredient Validation
  Customers should be able to create custom meals using available ingredients.
  The system must validate stock availability and ingredient selection before saving.

  Background:
    Given the Ingredients table has current stock levels
    And the user is logged in as a customer

  Scenario: Create a custom meal with available ingredients
    Given the Ingredients table has current stock levels
    And the ingredient "broccoli" stock is reset to 10.0
    And the user is logged in as a customer
    Given a customer selects chicken, rice, and broccoli
    When they submit the custom meal request
    Then the system should accept and save the customized order
    And deduct the used ingredient quantities from stock

  Scenario: Prevent use of unavailable ingredient
    Given the ingredient "avocado" is currently out of stock
    When a customer tries to add "avocado" to their custom meal
    Then the system should notify "Ingredient not found: avocado"


  Scenario: Reject custom meal with no ingredients
    Given a customer is on the custom meal creator page
    When they submit the form without selecting any ingredients
    Then the system should display the validation message "At least one ingredient must be selected"

  Scenario: Handle database error during custom meal save
    Given a customer selects chicken and rice
    When a database error occurs during the save
    Then the system should display the system error message "Failed to save custom meal due to system error"

  Scenario: Custom meal with substitutions triggers chef notification
    Given a customer selects chicken and rice
    And they substitute "chicken" with "tofu"
    When they submit the custom meal request
    Then the system should accept and save the customized order

  Scenario: Reject custom meal when stock is below required threshold
    Given the ingredient "broccoli" has low stock
    When a customer tries to add "broccoli" to their custom meal
    Then the system should notify "Broccoli is unavailable"
