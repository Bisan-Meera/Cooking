Feature: Custom Meal Creation and Ingredient Validation
  Customers should be able to create custom meals using available ingredients.
  The system must validate stock availability and ingredient selection before saving.

  Background:
    Given the Ingredients table has current stock levels
    And the user is logged in as a customer

  Scenario: Create a custom meal with available ingredients
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
    Then the system should show "At least one ingredient must be selected"

  Scenario: Handle database error during custom meal save
    Given a customer selects chicken and rice
    When a database error occurs during the save
    Then the system should display "At least one ingredient must be selected"

