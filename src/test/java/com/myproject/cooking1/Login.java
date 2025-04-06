package com.myproject.cooking1;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class Login {

    private static final Set<Integer> loggedInUsers = new HashSet<>();
    private String lastMessage = "";

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
            lastMessage = "User is already logged in";
        } else {
            lastMessage = "User is not logged in";
        }
    }

    @Then("the system should prevent the login and display {string}")
    public void theSystemShouldPreventTheLoginAndDisplay(String expectedMessage) {
        assert lastMessage.equals(expectedMessage);
        System.out.println(lastMessage);
    }

    @Given("user with user_id {int} and name {string} is not logged in")
    public void userWithUserIdAndNameIsNotLoggedIn(Integer userId, String name) {
        loggedInUsers.remove(userId);
    }

    @When("they enter valid credentials: user_id {int} and name {string}")
    public void theyEnterValidCredentialsUserIdAndName(Integer userId, String name) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT role FROM Users WHERE user_id = ? AND name = ?"
            );
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                loggedInUsers.add(userId);
                lastMessage = role + " Dashboard";
            } else {
                lastMessage = "Invalid user_id or name";
            }

        } catch (Exception e) {
            e.printStackTrace();
            lastMessage = "Login failed due to system error";
        }
    }

    @Then("they should be redirected to the {string}")
    public void theyShouldBeRedirectedToThe(String expectedDashboard) {
        assert lastMessage.equals(expectedDashboard);
        System.out.println("Redirected to: " + lastMessage);
    }

    @Given("user is not logged in")
    public void userIsNotLoggedIn() {
        loggedInUsers.clear();
    }

    @When("they enter user_id {int} and name {string}")
    public void theyEnterUserIdAndName(Integer userId, String name) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM Users WHERE user_id = ? AND name = ?"
            );
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                lastMessage = "Invalid user ID or name";
            }

        } catch (Exception e) {
            lastMessage = "Login failed due to system error";
        }
    }

    @Then("the system should show {string}")
    public void theSystemShouldShow(String expectedMessage) {
        assert lastMessage.equals(expectedMessage);
        System.out.println(lastMessage);
    }

    @When("they enter user_id {int} and invalid name {string}")
    public void theyEnterUserIdAndInvalidName(Integer userId, String name) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM Users WHERE user_id = ? AND name = ?"
            );
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                lastMessage = "Invalid user ID or name";  // ✅ Set the message here
            } else {
                lastMessage = "Logged in successfully";  // Optional: fallback
            }

        } catch (Exception e) {
            lastMessage = "Database error occurred";
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
            lastMessage = "User ID and name cannot be empty";
        } else {
            try {
                int userId = Integer.parseInt(userIdStr);
                theyEnterUserIdAndName(userId, name);
            } catch (NumberFormatException e) {
                lastMessage = "Invalid user_id format";
            }
        }
    }

    @Then("the system should display {string}")
    public void theSystemShouldDisplay(String expectedMessage) {
        assert lastMessage.equals(expectedMessage);
        System.out.println(lastMessage);
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
            lastMessage = "Login failed due to system error";
        }
    }
}
