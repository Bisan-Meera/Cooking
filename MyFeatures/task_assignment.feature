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

