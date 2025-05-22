Feature: Supplier & Restocking System
  The system should link ingredients with suppliers and automate restocking workflows.

  Background:
    Given the following supplier exists:
      | supplier_id | name              | contact_info       |
      | 1           | Al-Madina Fresh   | +966500112233      |

  Scenario: Link new ingredient to supplier
    Given a supplier "Al-Madina Fresh" is added
    When the admin links them with "Mint Leaves" at 5.00 per unit
    Then the ingredient should be linked in the database
    And their pricing and supply data should be saved

  Scenario: Generate restock list
    Given the system detects 5 low-stock ingredients
    When the admin clicks "Generate Order List"
    Then the system should create a suggested purchase list for those items

  Scenario: Edit supplier details
    Given a supplier's phone number changed to "+966511223344"
    When the admin updates Al-Madina Fresh's contact info
    Then the change should be saved in the database
    And future purchase orders should use the updated contact info
