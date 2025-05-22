Feature: Suggest Ingredient Substitutions Based on Dietary Restrictions or Unavailability

  As a customer, I want the system to suggest alternative ingredients
  if an ingredient is unavailable or restricted based on my dietary needs.

  As a chef, I want to be notified when a substitution occurs
  so I can approve or modify the custom meal.

  Background:
    Given the Ingredients table has current stock levels
    And the user is logged in as a customer
    And a customer is on the custom meal creator page

  Scenario: Suggest substitution for restricted ingredient
    Given the ingredient "Beef" is restricted for the customer
    When the customer selects "Beef" for a custom meal
    Then the system should suggest one of the following:
      | Tofu             |
      | Pita Bread       |
      | Chickpeas        |
      | Rice noodles     |
      | Cabbage          |
      | Parsley          |
      | Bread            |
      | Garlic           |
      | Tomato sauce     |
      | Basmati rice     |
      | Tomatoes         |
      | Olive oil        |
      | Bell peppers     |
      | Onions           |
      | Mozzarella       |
      | Lasagna sheets   |
      | Broccoli         |
      | Rice             |

    And the substitution should be recorded for "Beef"
    And notify the chef to review the substitution
    When the kitchen staff assigns the task to chef with id 3

  Scenario: Suggest substitution for out-of-stock ingredient
    Given the ingredient "Salmon" is currently out of stock
    And the ingredient "Salmon" is not restricted for the customer
    When the customer selects "Salmon" for a custom meal
    Then the system should suggest one of the following:
      | Tofu             |
      | Pita Bread       |
      | Chickpeas        |
      | Rice noodles     |
      | Cabbage          |
      | Parsley          |
      | Bread            |
      | Garlic           |
      | Tomato sauce     |
      | Basmati rice     |
      | Tomatoes         |
      | Olive oil        |
      | Bell peppers     |
      | Onions           |
      | Mozzarella       |
      | Lasagna sheets   |
      | Broccoli         |
      | Rice             |


    And the substitution should be recorded for "Salmon"
    And notify the chef to review the substitution
    When the kitchen staff assigns the task to chef with id 3

  Scenario: No substitution needed when ingredient is valid
    Given the ingredient "Rice" is in stock and not restricted
    When the customer selects "Rice" for a custom meal
    Then the system should accept the ingredient without substitution
    And the chef should not be notified
