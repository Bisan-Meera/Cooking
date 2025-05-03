Feature: Notification Center
  The system should notify users about low stock, ready meals, and upcoming tasks or deliveries.

  # --- Feature 6.1: Reminders for Customers and Chefs ---

  Scenario: Meal delivery reminder to customer
    Given a meal is scheduled for delivery in less than 1 hour
    When the system checks upcoming deliveries
    Then the customer should receive a reminder notification

  Scenario: Meal ready alert to customer
    Given a meal is prepared and packed
    When the chef marks it as "Ready"
    Then the customer should receive a notification with pickup/delivery info

  Scenario: Task reminder to chef
    Given a chef has a pending task due in 30 minutes
    When the system checks for upcoming tasks
    Then it sends a reminder alert to the chef


  # --- Feature 6.2: Low-Stock Notifications for Kitchen Manager ---

  Scenario: Low stock alert to kitchen manager
    Given the stock of "Milk" drops below threshold
    When the system updates inventory
    Then a notification should be sent to all kitchen staff members

  Scenario: Restocking suggestion after ingredient use
    Given an ingredient is used in an order and its quantity drops below threshold
    When the stock update is processed
    Then the system creates a restocking notification
    And marks it as unread with the current timestamp
