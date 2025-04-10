Feature: Task Assignment Panel
  Kitchen managers (kitchen staff) should be able to assign and manage cooking-related tasks for chefs and kitchen staff.

  Background:
    Given the following users exist:
      | user_id | name            | role           |
      | 3       | Chef Yasser     | chef           |
      | 4       | Fatima Ibrahim  | kitchen_staff  |
    And the following orders exist:
      | order_id | customer_id | status   |
      | 101      | 1           | pending  |

  Scenario: Assign task to chef
    Given Fatima Ibrahim is logged in as kitchen manager
    When she assigns task "Cook Pasta" to chef Yasser for order 101
    Then the task should be saved in the system
    And it should appear on Yasser’s dashboard

  Scenario: Mark task as completed
    Given chef Yasser has a task "Cook Pasta" for order 101 with status "pending"
    When he marks the task as completed
    Then the task status should be updated to "Done"
    And the update should be reflected on his task list

  Scenario: View task list
    Given a kitchen staff member is logged in
    When they visit their tasks tab
    Then all assigned tasks should be listed with status and order ID
