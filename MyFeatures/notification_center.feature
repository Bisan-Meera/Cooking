Feature: Notification Center
  The system should notify users about low stock, ready meals, and pending tasks.

  Scenario: Low stock alert to kitchen staff
    Given the stock of "Milk" drops below threshold
    When the system updates inventory
    Then a notification should be sent to all kitchen staff members

  Scenario: Meal ready alert to customer
    Given a meal is prepared and packed
    When the chef marks it as "Ready"
    Then the customer should receive a notification with pickup/delivery info

  Scenario: Task reminder to chef
    Given a chef has a pending task due in 30 minutes
    When the system checks for upcoming tasks
    Then it sends a reminder alert to the chef
