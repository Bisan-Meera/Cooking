Feature: Scheduling and Task Assignment

  As a kitchen manager,
  I want to assign tasks to chefs based on their workload and expertise,
  So that I can ensure balanced workloads and efficient kitchen operations.

  As a chef,
  I want to receive notifications about my assigned cooking tasks,
  So that I can prepare meals on time.

  Background:
    Given the system has chefs with different workloads and expertise levels
    And there are pending cooking tasks in the kitchen

  # Existing scenarios (core flow)
  Scenario: Assign a task to the chef with the least workload
    When the kitchen manager assigns a new cooking task
    Then the task should be assigned to the chef with the least workload

  Scenario: Assign a task to a chef with matching expertise
    Given a cooking task requires "Italian" cuisine expertise
    When the kitchen manager assigns the task
    Then the task should be assigned to a chef with "Italian" expertise

  Scenario: Notify chef upon task assignment
    When a task is assigned to a chef
    Then the chef should receive a notification about the task

  Scenario: Prevent assigning multiple tasks to overworked chefs
    Given Chef John already has 5 active tasks
    When the kitchen manager tries to assign a new task
    Then the task should be assigned to another available chef

  # **Coverage Improvement Scenarios Below**

  Scenario: No chefs are available for assignment
    Given there are no chefs available in the system
    When the kitchen manager assigns a new cooking task
    Then the system should indicate that no assignment is possible

  Scenario: Multiple chefs with equal workload (tie-break)
    Given two or more chefs have the same lowest workload
    When the kitchen manager assigns a new cooking task
    Then the system should assign the task to one of the least loaded chefs

  Scenario: Assign a task when no chef has the required expertise
    Given a cooking task requires "Japanese" cuisine expertise
    When the kitchen manager assigns the task
    Then the system should indicate that no chef with the required expertise is available

  Scenario: Assign a task with empty or null expertise
    Given a cooking task requires "" cuisine expertise
    When the kitchen manager assigns the task
    Then the system should indicate that no chef with the required expertise is available

  Scenario: Chef receives notification after task assignment
    When the kitchen manager assigns a new cooking task
    Then the assigned chef should have a notification containing the task ID

  Scenario: Mark a task as ready and notify the customer
    Given a task is assigned to a chef and linked to a customer order
    When the chef marks the task as ready
    Then the customer should receive a "meal is ready" notification

  Scenario: Assign a specific task to a specific chef
    Given a specific cooking task and a specific chef are available
    When the kitchen manager assigns the task to that chef
    Then the task should be assigned and the chef notified

  Scenario: Attempt to assign a task with invalid IDs or database failure
    Given the database is unavailable or returns an error
    When the kitchen manager tries to assign a new cooking task
    Then the system should handle the error gracefully and indicate assignment failed

  Scenario: Display all pending tasks with linked order details
    When the kitchen manager requests to view all pending tasks
    Then all pending tasks and their linked order or meal details should be displayed

  Scenario: Display all active tasks for a chef
    Given Chef Maria is logged in
    When she requests to view her active tasks
    Then the system should list all active cooking tasks assigned to her

  Scenario: Get the number of active tasks for a chef
    Given Chef Luca is available
    When the kitchen manager requests the task count for Chef Luca
    Then the system should return the number of active tasks assigned to Chef Luca


  Scenario: Database failure when assigning a task to the least loaded chef
    Given the database is unavailable or returns an error
    When the kitchen manager tries to assign a new cooking task
    Then the system should handle the error gracefully and indicate assignment failed

  Scenario: Database failure when assigning a task to a chef with required expertise
    Given the database is unavailable or returns an error
    And a cooking task requires "Sushi" cuisine expertise
    When the kitchen manager assigns the task
    Then the system should handle the error gracefully and indicate assignment failed

  Scenario: No chef with the required expertise is available
    Given the system has chefs with different workloads and expertise levels
    And a cooking task requires "Lebanese" cuisine expertise
    When the kitchen manager assigns the task
    Then the system should indicate that no chef with the required expertise is available

  Scenario: Getting task count for non-existent chef returns zero
    Given there are no chefs available in the system
    When the kitchen manager requests the task count for Chef Luca
    Then the system should return the number of active tasks assigned to Chef Luca

  Scenario: Get chef expertise for non-existent chef returns null
    Given there are no chefs available in the system
    When the kitchen manager requests the expertise for chef id 9999
    Then the system should receive no expertise

  Scenario: Display all pending tasks with details using print method
    When the kitchen manager requests to print all pending tasks
    Then the system should display pending tasks with details in the console

  Scenario: Display all active tasks for a chef using print method
    Given Chef Maria is logged in
    When she requests to print her active tasks
    Then the system should display her active cooking tasks in the console

  Scenario: Mark a task as ready when task not linked to customer order
    Given the system has chefs with different workloads and expertise levels
    And there are pending cooking tasks in the kitchen
    When the kitchen manager assigns a new cooking task
    When the chef marks the task as ready (minimal)
    Then the task should be marked ready without errors

  Scenario: View pending tasks when there are no pending tasks
    Given there are no chefs available in the system
    When the kitchen manager requests to view all pending tasks
    Then the system should indicate no pending tasks clearly

  Scenario: Chef views active tasks when no active tasks assigned
    Given the system has chefs with different workloads and expertise levels
    And Chef John already has 0 active tasks
    When Chef John requests to view his active tasks
    Then the system should indicate no active tasks clearly

  Scenario: Get all chefs with workload and expertise returns empty list when no chefs exist
    Given there are no chefs available in the system
    When the kitchen manager requests the list of chefs with workload and expertise
    Then the system should receive an empty list

  Scenario: Assignment fails when database insert for new task fails
    Given the database is unavailable or returns an error
    When the kitchen manager assigns a new cooking task
    Then the system should handle the error gracefully and indicate assignment failed

  Scenario: View pending tasks with regular and custom orders
    Given there are pending cooking tasks with regular and custom orders
    When the kitchen manager requests to view all pending tasks
    Then all pending tasks and their linked order or meal details should be displayed correctly

  Scenario: View pending tasks with a custom order having no ingredients
    Given the system has chefs with different workloads and expertise levels
    And there are pending cooking tasks with a custom order having no ingredients
    When the kitchen manager requests to view all pending tasks
    Then all pending tasks and their linked order details should be displayed with no ingredient details for the custom order

  Scenario: Assign a cooking task to a specific chef
    Given there is a chef available in the system
    And there is a pending cooking task in the kitchen
    When the kitchen manager assigns the task to the chef
    Then the task should be successfully assigned to the chef

  Scenario: Attempt to assign an invalid cooking task to a chef
    Given there is a chef available in the system
    When the kitchen manager attempts to assign an invalid task to the chef
    Then the task assignment should fail

  Scenario: Attempt to assign a cooking task to an invalid chef
    Given there is a pending cooking task in the kitchen
    When the kitchen manager attempts to assign the task to an invalid chef
    Then the task remains unassigned in the database
