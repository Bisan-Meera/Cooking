Feature: Manage dietary preferences and allergies

  As a customer,
  I want to add and update my dietary preferences and allergies,
  So that the system can recommend appropriate meals and avoid unwanted ingredients.

  Scenario: Add dietary preferences for the first time
    Given the customer is logged in
    When the customer navigates to the preferences page
    And the customer enters "vegetarian" as a dietary preference and "nuts" as an allergy
    And the customer saves the preferences
    Then the system should store the dietary preference as "vegetarian"
    And the system should store the allergy as "nuts"

  Scenario: Update existing preferences
    Given the customer has already saved preferences
    When the customer updates the dietary preference to "vegan"
    And removes "nuts" from the allergy list
    Then the system should update the preferences accordingly

  Scenario: View saved preferences
    Given the customer is logged in
    When the customer navigates to the preferences page
    Then the system should display the customer's current dietary preferences and allergies
