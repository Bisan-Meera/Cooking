Feature: Order & Invoice Manager
  The system should handle orders and generate invoices and reports for admin review.

  Background:
    Given the following customer exists:
      | user_id | name          | role     |
      | 1       | Layla Hassan  | customer |

  Scenario: Place order with multiple items
    Given customer Layla Hassan adds 3 meals to the cart
    When she proceeds to checkout
    Then a new order and corresponding invoice should be generated

  Scenario: View past invoice
    Given Layla Hassan placed an order last week
    When she checks the invoice tab
    Then she should be able to view and download the invoice

  Scenario: Admin views revenue report
    Given multiple orders were completed today
    When the admin clicks on “Daily Summary”
    Then a report showing total revenue and meal counts should be generated
