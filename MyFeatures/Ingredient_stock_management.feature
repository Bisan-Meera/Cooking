Feature: Real-time Ingredient Stock Management and Restocking Suggestions

  Background:
    Given the system tracks ingredient stock levels in the "ingredients" table
    And each ingredient has a defined "threshold" quantity
    And kitchen managers are users with role "kitchen_manager"
    And the system sends notifications through the "notifications" table
    And stock levels are updated when orders are placed

  Scenario: Update stock levels in real-time when an order is placed
    Given a customer places an order containing meals with specific ingredients
    When the order is confirmed
    Then the system deducts the required quantity of each ingredient from the "stock_quantity"
    And updates the "last_updated" timestamp for each affected ingredient

  Scenario: Automatically suggest restocking when stock falls below threshold
    Given an ingredient's "stock_quantity" falls below or equals its "threshold"
    When the stock update occurs
    Then the system creates a restocking notification for the kitchen manager
    And the notification contains the ingredient name, current quantity, and restocking suggestion
    And the notification is marked as unread with the current timestamp

  Scenario: Kitchen manager reviews low stock notifications
    Given the kitchen manager is logged into the system
    When they open the notifications panel
    Then they should see all unread restocking notifications
    And each notification displays the ingredient name, current stock, and suggested action
    When the manager views a notification
    Then the notification's "is_read" status is updated to true

  Scenario: Manual stock adjustment by kitchen manager
    Given a kitchen manager wants to update an ingredient's stock
    When they adjust the "stock_quantity" in the inventory system
    Then the "ingredients" table reflects the new quantity
    And the "last_updated" timestamp is updated
    And previous low-stock notifications for that ingredient may be marked as resolved

  Scenario: Handle customized orders affecting ingredient stock
    Given a customer places a customized order with ingredient substitutions
    When the system processes the customized order
    Then the system deducts the quantity from the substituted ingredient if a substitution exists
    And the system deducts from the default ingredient if no substitution exists
    And updates stock quantities and timestamps accordingly
    And suggests restocking if the substituted or original ingredient falls below the threshold

  Scenario: Deduct stock when an order is confirmed
    And the initial stock for ingredient "Onions" is recorded
    And order ID 1 exists with meals and ingredients
    When the order is confirmed
    Then ingredient "Onions" should have stock 25.67
    And the "last_updated" timestamp for ingredient "Onions" should be recent


  Scenario: Create restocking notification when stock falls below threshold
    Given ingredient "Onions" stock is reset to 0.3
    And order ID 1 exists with meals and ingredients
    When the order is confirmed
    Then a restocking notification for "Onions" should be sent to kitchen staff

  Scenario: Identify low-stock ingredients
    Given ingredient "rice" stock is reset to 0.3
    And ingredient "rice" has a threshold of 0.5
    When the system checks for low-stock ingredients
    Then the result should include "rice"

  Scenario: Kitchen staff manually updates an ingredient's stock
    Given ingredient "Tomatoes" stock is reset to 20.0
    When kitchen staff sets the stock of "Tomatoes" to 35.5
    Then ingredient "Tomatoes" should have stock 35.5

  Scenario: Check if an ingredient is below threshold
    Given ingredient "Garlic" stock is reset to 0.2
    And ingredient "Garlic" has a threshold of 0.5
    When the system checks if "Garlic" is below its threshold
    Then the result should be "true"

  Scenario: Retrieve ingredient name by ID
    When the system retrieves the name of ingredient ID 10
    Then the ingredient name should be "Garlic"
