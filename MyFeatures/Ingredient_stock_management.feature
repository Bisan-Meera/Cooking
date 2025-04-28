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
