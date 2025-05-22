Feature: Customer Profile & Preferences
  Customers should be able to manage their dietary preferences and allergies to receive personalized meal suggestions.

  Background:
    Given the Users table contains customers
    And the Meals and Ingredients are defined in the system
    And the user is logged in

  Scenario: Add dietary preferences
    Given a logged-in customer with user_id 1 and name "Layla Hassan" is on the profile settings page
    When they select "Vegetarian" and "No Nuts" as preferences
    Then these preferences should be saved to their profile
    And future meal suggestions should match these preferences

  Scenario: View saved preferences
    Given a logged-in customer with user_id 1 has previously saved dietary preferences
    When they visit the preferences tab
    Then the saved preferences should be pre-filled and visible

  Scenario: Edit allergies
    Given a logged-in customer with user_id 2 has previously entered "no allergies"
    When they update the allergy information to "Allergic to seafood"
    Then their profile should reflect the updated allergy
    And future meal suggestions should exclude meals containing seafood ingredients

  Scenario Outline: Submit empty preferences
    Given a logged-in customer with user_id <UserID> is on the profile settings page
    When they leave the preferences fields empty and submit the form
    Then the system should show "Preferences cannot be empty"

    Examples:
      | UserID |
      | 1      |
      | 2      |

  Scenario Outline: Database error while saving preferences
    Given a logged-in customer with user_id <UserID> is on the preferences page
    When they select dietary preferences and a database error occurs while saving
    Then the system should show "Unable to save preferences due to system error"

    Examples:
      | UserID |
      | 1      |
      | 2      |

  Scenario: Exception occurs while saving preferences
    Given a logged-in customer with user_id 3 is on the profile settings page
    When they try to save preferences and a simulated DB failure occurs
    Then the system should show "Database error while updating preferences"

  Scenario: Exception occurs while viewing preferences
    Given a logged-in customer with user_id 3 is on the preferences page
    When they try to view preferences and the database fails
    Then the system should show "Database error while fetching preferences"

  Scenario: Viewing preferences when none are saved
    Given a logged-in customer with user_id 667 is on the preferences page
    When they view preferences without any saved data
    Then the system should return blank preferences

  Scenario: Submit form with only preference filled
    Given a logged-in customer with user_id 103 is on the preferences page
    When they set the preference to "Halal" and leave allergy empty
    And they submit the profile form
    Then the system should show "Preferences saved successfully"

  Scenario: Submit form with only allergy filled
    Given a logged-in customer with user_id 143 is on the preferences page
    When they set the allergy to "Peanuts" and leave preference empty
    And they submit the profile form
    Then the system should show "Preferences saved successfully"


  Scenario: Submit form with null preference and allergy
    Given a logged-in customer with user_id 3000 is on the preferences page
    When they set both preference and allergy to null
    And they submit the profile form
    Then the system should show "Preferences cannot be empty"

  Scenario: Retry submit after DB failure
    Given a logged-in customer with user_id 3030 is on the preferences page
    When they simulate a DB failure and submit preference "Vegetarian"
    Then the system should show "Unable to save preferences due to system error"
    When they retry submitting with DB OK
    Then the system should show "Preferences saved successfully"


  Scenario: Submit preferences with extra spaces
    Given a logged-in customer with user_id 3085 is on the preferences page
    When they set the preference to " Vegan " and the allergy to " Dairy "
    And they submit the profile form
    Then the system should show "Preferences saved successfully"


  Scenario: Exception occurs with blank error message while saving preferences
    Given a logged-in customer with user_id 404 is on the preferences page
    When they try to save preferences and a blank error occurs
    Then the system should show "Unable to save preferences due to system error"

  Scenario: Exception occurs with null error message while saving preferences
    Given a logged-in customer with user_id 405 is on the preferences page
    When they try to save preferences and a null error occurs
    Then the system should show "Unable to save preferences due to system error"


  Scenario: Checked exception occurs while saving preferences
    Given a logged-in customer with user_id 406 is on the preferences page
    When they try to save preferences and a checked exception occurs
    Then the system should show "checked exception!"
