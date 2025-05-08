Feature: AI-Powered Recipe Recommendations
  The system should recommend suitable meals to users based on preferences, allergies, available ingredients, and prep time.

  Background:
    Given the following customer preferences exist:
      | user_id | dietary_preference | allergy        |
      | 101     | Vegan              | Peanuts        |
      | 102     | Vegetarian         | Gluten         |
      | 103     | Halal              | None           |

    And the meals have preparation times and mapped ingredients
    And the stock levels for ingredients are up to date

  Scenario: Recommend a vegan recipe under 30 minutes using tomatoes, basil, and pasta
    Given user 101 wants a vegan recipe
    And has 30 minutes available
    And has the following ingredients:
      | Tomatoes |
      | Basil    |
      | Pasta    |
    When the AI assistant checks for suitable meals
    Then the assistant should recommend "Vegan Pesto Pasta"
    And explain it uses available ingredients, fits time, and meets dietary restrictions

  Scenario: Recommend a vegetarian recipe for user with gluten allergy
    Given user 102 wants a vegetarian recipe
    And is allergic to gluten
    And has the following ingredients:
      | Tomatoes |
      | Rice     |
      | Cheese   |
    When the AI assistant filters the meal database
    Then the assistant should only show gluten-free vegetarian options
    And exclude meals with ingredients like bread or lasagna sheets



  Scenario: No valid meal found
    Given user 101 wants a vegan recipe
    And has only the following ingredients:
      | Beef |
      | Lamb |
    When the AI assistant checks for suitable options
    Then it should respond "No valid recipes found"
    And suggest buying alternative vegan ingredients
