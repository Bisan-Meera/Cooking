Feature: Sign Up
  Only new customers should be able to register themselves using valid information.

  Background:
    Given the Users table is available

  Scenario Outline: Successful customer sign-up with valid data
    Given the email "<Email>" is not already registered
    When the user signs up with name "<Name>", email "<Email>", password "<Password>", role "<Role>", and expertise "<Expertise>"
    Then the system should create a new user and show "Registration successful"

    Examples:
      | Name       | Email            | Password | Role     | Expertise |
      | John Doe   | john1@example.com | 1234     | customer |           |

  Scenario Outline: Sign-up with missing fields
    Given the Users table is available
    When the user tries to sign up with missing info: name "<Name>", email "<Email>", password "<Password>", role "<Role>"
    Then the signup result should be "All required fields must be filled"

    Examples:
      | Name    | Email            | Password | Role     |
      |         | john@example.com | 1234     | customer |
      | John    |                  | 1234     | customer |
      | John    | john@example.com |          | customer |
      | John    | john@example.com | 1234     |          |

  Scenario: Sign-up with already used email
    Given the email "layla@example.com" is already registered
    When the user tries to sign up again with that email
    Then the signup result should be "Email already in use"

  Scenario Outline: Sign-up with invalid or restricted role
    Given the Users table is available
    When the user signs up with name "Test User", email "<Email>", password "test123", role "<Role>", and expertise "<Expertise>"
    Then the signup result should be "Invalid role specified"

    Examples:
      | Email              | Role           | Expertise |
      | chef@kitchen.com   | chef           | Italian   |
      | admin@control.com  | admin          |           |
      | sam@kitchen.com    | kitchen_staff  |           |
      | user@domain.com    | manager        |           |
