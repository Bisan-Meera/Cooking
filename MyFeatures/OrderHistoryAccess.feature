Feature: Accessing customer order history

  The system should allow different users to interact with customer order history
  to support meal reordering, personalization, and data analysis.

  # --- Customer perspective ---
  Scenario: Customer views their past meal orders
    Given the customer "Layla Hassan" with user_id 1 has ordered "Vegetarian Lasagna"
    When she logs in and goes to her order history page
    Then she should see "Vegetarian Lasagna" listed with price and description

  Scenario: Customer with no previous orders sees an empty message
    Given the customer "John Doe" with user_id 142 has no past orders
    When he logs in and goes to the order history page
    Then he should see a message saying "You have not placed any orders yet"

  # --- Chef perspective ---
  Scenario: Chef views order history of a specific customer
    Given the chef "Chef Yasser" is logged in
    And customer "Layla Hassan" has placed multiple orders
    When the chef selects "Layla Hassan" from the customer list
    Then he should see all meals she has ordered in the past

  # --- Admin perspective ---
  Scenario: Admin retrieves all customer order history for analysis
    Given the admin "Sara" is logged in
    When she accesses the system order analytics dashboard
    Then she should be able to retrieve all orders placed by all customers
