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
