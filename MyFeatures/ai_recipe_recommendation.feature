Feature: AI Recipe Recommendation
  The system should recommend a suitable meal using AI logic based on dietary restrictions, available ingredients, and time constraints.

  Background:
    Given the following meals exist:
      | meal_id | name                | description                          | price |
      | 1       | Spaghetti           | Pasta with tomato basil sauce        | 25.00 |
      | 2       | Tomato Basil Soup   | Creamy tomato and basil soup         | 20.00 |
      | 3       | Vegan Pesto Pasta   | Pasta with olive oil and garlic      | 23.00 |
    And their ingredients are defined in the Meal_Ingredients table
    And the ingredients include:
      | ingredient_id | name         |
      | 1             | Tomatoes     |
      | 2             | Basil        |
      | 3             | Pasta        |
      | 4             | Olive Oil    |
      | 5             | Garlic       |

  Scenario: Recommend a vegan meal using available ingredients
    Given a user with a "Vegan" dietary restriction
    And the user has Tomatoes, Basil, and Pasta available
    And they have 30 minutes to cook
    When the system evaluates all meals from the Meals table
    Then it should recommend "Spaghetti"
    And explain: "Spaghetti includes only your available ingredients, meets your vegan preference, and takes less than 30 minutes."
