package com.myproject.cooking1;

import com.myproject.cooking1.entities.TestContext;
import com.myproject.cooking1.entities.User;
import io.cucumber.java.en.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class Login {

    private static final Set<Integer> loggedInUsers = new HashSet<>();

    User fetchedUser; // already declared at top
    User newUser;
    List<Integer> fetchedIds;

    @Given("the Users table contains existing users")
    public void theUsersTableContainsExistingUsers() {
        System.out.println("Assuming the Users table is already populated.");
    }

    @Given("user with user_id {int} and name {string} is already logged in")
    public void userWithUserIdAndNameIsAlreadyLoggedIn(Integer userId, String name) {
        loggedInUsers.add(userId);
    }

    @When("they try to log in again with user_id {int} and name {string}")
    public void theyTryToLogInAgainWithUserIdAndName(Integer userId, String name) {
        if (loggedInUsers.contains(userId)) {
            TestContext.set("lastMessage", "User is already logged in");
        } else {
            TestContext.set("lastMessage", "User is not logged in");
        }
    }

    @Then("the system should prevent the login and display {string}")
    public void theSystemShouldPreventTheLoginAndDisplay(String expectedMessage) {
        String actual = TestContext.get("lastMessage", String.class);
        assertEquals(expectedMessage, actual);
        System.out.println(actual);
    }
    @Given("Chef Yasser is present in the database")
    public void chefYasserIsPresentInDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
           // conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Users (user_id, name, email, password, role, expertise) " +
                            "VALUES (3, 'Chef Yasser', 'yasser@example.com', 'pass', 'chef', 'French') " +
                            "ON CONFLICT (user_id) DO NOTHING"
            );
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Given("user with user_id {int} and name {string} is not logged in")
    public void userWithUserIdAndNameIsNotLoggedIn(Integer userId, String name) {
        loggedInUsers.remove(userId);
    }

    @When("they enter valid credentials: user_id {int} and name {string}")
    public void theyEnterValidCredentialsUserIdAndName(Integer userId, String name) {
        try (Connection conn = DBConnection.getConnection()) {
            fetchedUser = User.getUserByIdAndName(userId, name, conn); // ✅ assign here
            if (fetchedUser != null) {
                loggedInUsers.add(fetchedUser.getUserId());
                TestContext.set("lastMessage", fetchedUser.getRole() + " Dashboard");
            } else {
                TestContext.set("lastMessage", "Invalid user_id or name");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TestContext.set("lastMessage", "Login failed due to system error");
        }
    }


    @Then("they should be redirected to the {string}")
    public void theyShouldBeRedirectedToThe(String expectedDashboard) {
        String actual = TestContext.get("lastMessage", String.class);
        assertEquals(expectedDashboard, actual);
        System.out.println("Redirected to: " + actual);
    }

    @Given("user is not logged in")
    public void userIsNotLoggedIn() {
        loggedInUsers.clear();
    }

    @When("they enter user_id {int} and name {string}")
    public void theyEnterUserIdAndName(Integer userId, String name) {
        try (Connection conn = DBConnection.getConnection()) {
            User user = User.getUserByIdAndName(userId, name, conn);
            if (user == null) {
                TestContext.set("lastMessage", "Invalid user ID or name");
            }
        } catch (Exception e) {
            TestContext.set("lastMessage", "Login failed due to system error");
        }
    }

    @Then("the system should show {string}")
    public void theSystemShouldShow(String expectedMessage) {
        String actual = TestContext.get("lastMessage", String.class);
        assertEquals(expectedMessage, actual);
        System.out.println("Message: " + actual);
    }

    @When("they enter user_id {int} and invalid name {string}")
    public void theyEnterUserIdAndInvalidName(Integer userId, String name) {
        try (Connection conn = DBConnection.getConnection()) {
            User user = User.getUserByIdAndName(userId, name, conn);
            if (user == null) {
                TestContext.set("lastMessage", "Invalid user ID or name");
            } else {
                TestContext.set("lastMessage", "Logged in successfully");
            }
        } catch (Exception e) {
            TestContext.set("lastMessage", "Database error occurred");
            e.printStackTrace();
        }
    }

    @Given("the login page is displayed")
    public void theLoginPageIsDisplayed() {
        System.out.println("Login page loaded.");
    }

    @When("the user submits user_id {string} and name {string}")
    public void theUserSubmitsUserIdAndName(String userIdStr, String name) {
        if (userIdStr.isBlank() || name.isBlank()) {
            TestContext.set("lastMessage", "User ID and name cannot be empty");
        } else {
            try {
                int userId = Integer.parseInt(userIdStr);
                theyEnterUserIdAndName(userId, name);
            } catch (NumberFormatException e) {
                TestContext.set("lastMessage", "Invalid user_id format");
            }
        }
    }

    @Then("the system should display {string}")
    public void theSystemShouldDisplay(String expectedMessage) {
        String actual = TestContext.get("lastMessage", String.class);
        assertEquals(expectedMessage, actual);
        System.out.println(actual);
    }

    @Given("the user is not logged in")
    public void theUserIsNotLoggedInAgain() {
        loggedInUsers.clear();
    }

    @When("they try to log in with user_id {int} and name {string} and the database connection fails")
    public void theyTryToLogInWithUserIdAndNameAndTheDatabaseConnectionFails(Integer userId, String name) {
        try {
            throw new RuntimeException("Simulated DB failure");
        } catch (Exception e) {
            TestContext.set("lastMessage", "Login failed due to system error");
        }
    }

    @Given("the user with ID {int} exists")
    public void theUserWithIDExists(int userId) {
        // Already inserted in background
    }

    @When("the system fetches user by ID {int}")
    public void theSystemFetchesUserByID(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            fetchedUser = User.getUserById(userId, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("the fetched user name should be {string}")
    public void theFetchedUserNameShouldBe(String expectedName) {
        assertEquals(expectedName, fetchedUser.getName());
    }

    @Then("the user expertise should be {string}")
    public void theUserExpertiseShouldBe(String expectedExpertise) {
        assertEquals(expectedExpertise, fetchedUser.getExpertise());
    }


    @When("a new user {string} is created")
    public void aNewUserIsCreated(String name) {
        try (Connection conn = DBConnection.getConnection()) {
            newUser = new User(99, name, "test@example.com", "1234", "customer", null);
            User.createUser(newUser, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("the system should be able to fetch them by ID")
    public void theSystemShouldBeAbleToFetchThemByID() {
        try (Connection conn = DBConnection.getConnection()) {
            User fetched = User.getUserById(99, conn);
            assertEquals(newUser.getName(), fetched.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @When("the system fetches all user IDs with role {string}")
    public void theSystemFetchesAllUserIDsWithRole(String role) {
        fetchedIds = User.getUserIdsByRole(role);
    }

    @Then("the result should include user ID {int}")
    public void theResultShouldIncludeUserID(int userId) {
        assert fetchedIds.contains(userId);
    }

    @Then("the system should confirm the user is a chef")
    public void theSystemShouldConfirmTheUserIsChef() {
        assert fetchedUser.isChef();
        assert !fetchedUser.isAdmin();
        assert !fetchedUser.isCustomer();
        assert !fetchedUser.isKitchenStaff();
    }




}