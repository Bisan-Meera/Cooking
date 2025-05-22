Feature: Supplier integration for real-time ingredient pricing and automated purchasing

  To ensure efficient kitchen management and cost control,
  the system should integrate with suppliers for real-time pricing
  and automatically manage purchasing when stock levels are critically low.

  Background:
    Given the system tracks ingredient stock levels in the "ingredients" table
    And suppliers provide real-time pricing through the "supplier_prices" table
    And purchase orders are recorded in the "purchase_orders" table
    And kitchen managers are users with role "Kitchen Manager"

  # -- Scenario 1: Kitchen manager views real-time ingredient prices
  Scenario: Kitchen manager checks real-time ingredient prices
    Given the kitchen manager is logged into the system
    When they access the supplier pricing dashboard
    Then they should see a list of ingredients with their current real-time prices
    And each ingredient entry shows the supplier name, current price, and last updated timestamp

  # -- Scenario 2: System detects critically low stock and generates a purchase order
  Scenario: Automatic purchase order generation when stock is low
    Given the system monitors stock levels periodically
    And an ingredient's quantity falls below its critical threshold
    When the stock monitoring job runs
    Then the system should generate a new purchase order in the "purchase_orders" table
    And the purchase order should include the ingredient name, quantity to reorder, supplier name, and current price
    And the purchase order should have a "Pending" status and current timestamp

  # -- Scenario 3: Kitchen manager reviews and approves purchase orders
  Scenario: Kitchen manager reviews pending purchase orders
    Given there are pending purchase orders in the system
    When the kitchen manager opens the purchase order management page
    Then they should see a list of all pending purchase orders
    And each purchase order displays ingredient details, supplier information, price, and quantity
    When the kitchen manager approves a purchase order
    Then the status of the purchase order is updated to "Approved"
    And a confirmation message is displayed

  # -- Scenario 4: Kitchen manager manually creates a purchase order
  Scenario: Kitchen manager manually orders an ingredient
    Given the kitchen manager needs to order an ingredient not automatically ordered
    When they select the ingredient and specify the quantity
    And they submit a manual purchase order request
    Then a new purchase order is created in the "purchase_orders" table with status "Pending"
    And the supplier price is fetched in real-time at the moment of submission

  # -- Scenario 5: Supplier prices update dynamically
  Scenario: Supplier updates an ingredient's price
    Given a supplier updates the price of an ingredient
    When the system receives the new price update
    Then the supplier_prices table is updated with the latest price and timestamp
    And any pending purchase orders for that ingredient are updated to reflect the new price

  Scenario: Check that pending purchase orders list is not empty
    Given there are pending purchase orders in the system
    When the kitchen manager views pending orders
    Then the list should contain at least one order summary
  # -- Scenario 6: Kitchen manager navigates the order management menu
  Scenario: Kitchen manager interacts with the purchase order submenu
    Given the kitchen manager opens the submenu with choices: "1", "5"
    Then the submenu should display pending orders and exit
