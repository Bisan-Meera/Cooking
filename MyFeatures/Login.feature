Feature:Login
  The system should allow registered users to log in using their user ID and name, and redirect them to the appropriate page based on their role.

  Background:
    Given the Users table contains existing users
    And Chef Yasser is present in the database
  Scenario: User already logged in
    Given user with user_id 1 and name "Layla Hassan" is already logged in
    When they try to log in again with user_id 1 and name "Layla Hassan"
    Then the system should prevent the login and display "User is already logged in"

  Scenario Outline: Valid credentials and role-based redirection
    Given user with user_id <UserID> and name "<Name>" is not logged in
    When they enter valid credentials: user_id <UserID> and name "<Name>"
    Then they should be redirected to the "<Role> Dashboard"

    Examples:
      | UserID | Name             | Role           |
      | 1      | Layla Hassan     | customer       |
      | 3      | Chef Yasser      | chef           |
      | 4      | Fatima Ibrahim   | kitchen_staff  |
      | 5      | Admin Sara       | admin          |

  Scenario Outline: Invalid user ID
    Given user is not logged in
    When they enter user_id <UserID> and name "<Name>"
    Then the system should show "Invalid user ID or name"

    Examples:
      | UserID | Name              |
      | 99     | Layla Hassan      |
      | 0      | Mohamed Said      |
      | 10     | Unknown Person    |

  Scenario Outline: Invalid name
    Given user is not logged in
    When they enter user_id <UserID> and invalid name "<Name>"
    Then the system should show "Invalid user ID or name"

    Examples:
      | UserID | Name              |
      | 1      | Wrong Name        |
      | 3      | Yasser Wrong      |
      | 5      | Admin Wrong       |

  Scenario Outline: Login with empty fields
    Given the login page is displayed
    When the user submits user_id "<UserID>" and name "<Name>"
    Then the system should display "User ID and name cannot be empty"

    Examples:
      | UserID | Name            |
      |        |                 |
      | 1      |                 |
      |        | Layla Hassan    |

  Scenario Outline: Login fails due to database error
    Given the user is not logged in
    When they try to log in with user_id <UserID> and name "<Name>" and the database connection fails
    Then the system should show "Login failed due to system error"

    Examples:
      | UserID | Name            |
      | 2      | Mohamed Said    |
      | 5      | Admin Sara      |

  Scenario: Retrieve user by ID
    Given the user with ID 3 exists
    When the system fetches user by ID 3
    Then the fetched user name should be "Chef Yasser"

  Scenario: Retrieve user expertise
    Given the user with ID 3 exists
    When the system fetches user by ID 3
    Then the user expertise should be "French"

  Scenario: Create a new user into the database
    When a new user "Test User" is created
    Then the system should be able to fetch them by ID

  Scenario: Fetch all user IDs with role chef
    When the system fetches all user IDs with role "chef"
    Then the result should include user ID 3

  Scenario: Verify role methods of logged-in user
    Given user with user_id 3 and name "Chef Yasser" is not logged in
    When they enter valid credentials: user_id 3 and name "Chef Yasser"
    Then the system should confirm the user is a chef
