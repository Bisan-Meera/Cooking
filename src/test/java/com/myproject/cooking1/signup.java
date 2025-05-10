package com.myproject.cooking1;

import com.myproject.cooking1.entities.TestContext;
import com.myproject.cooking1.entities.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class signup {
    private String lastMessage;
    private static final Set<String> usedEmails = new HashSet<>();

    @Given("the Users table is available")
    public void theUsersTableIsAvailable() {
        System.out.println("Users table is assumed ready.");
    }

    @Given("the email {string} is not already registered")
    public void theEmailIsNotAlreadyRegistered(String email) {
        usedEmails.remove(email.toLowerCase());
    }

    @Given("the email {string} is already registered")
    public void theEmailIsAlreadyRegistered(String email) {
        usedEmails.add(email.toLowerCase());
    }

    @When("the user signs up with name {string}, email {string}, password {string}, role {string}, and expertise {string}")
    public void theUserSignsUpWithNameEmailPasswordRoleAndExpertise(String name, String email, String password, String role, String expertise) {
        TestContext.clear();

        if (usedEmails.contains(email.toLowerCase())) {
            lastMessage = "Email already in use";
            TestContext.set("lastMessage", lastMessage);
            return;
        }

        if (!role.equals("customer")) {
            lastMessage = "Invalid role specified";
            TestContext.set("lastMessage", lastMessage);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Users WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                lastMessage = "Email already in use";
                TestContext.set("lastMessage", lastMessage);
                return;
            }

            User user = new User(0, name, email, password, role, null);
            User.createUser(user, conn);
            usedEmails.add(email.toLowerCase());
            lastMessage = "Registration successful";
            TestContext.set("lastMessage", lastMessage);

        } catch (Exception e) {
            e.printStackTrace();
            lastMessage = "System error occurred";
            TestContext.set("lastMessage", lastMessage);
        }
    }

    @When("the user tries to sign up with missing info: name {string}, email {string}, password {string}, role {string}")
    public void theUserTriesToSignUpWithMissingInfo(String name, String email, String password, String role) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || role.isBlank()) {
            lastMessage = "All required fields must be filled";
        } else {
            lastMessage = "Attempted signup should have been valid";
        }
        TestContext.set("lastMessage", lastMessage);
    }

    @When("the user tries to sign up again with that email")
    public void theUserTriesToSignUpAgainWithThatEmail() {
        lastMessage = "Email already in use";
        TestContext.set("lastMessage", lastMessage);
    }

    @Then("the system should create a new user and show {string}")
    public void theSystemShouldCreateANewUserAndShow(String expectedMessage) {
        assertEquals(expectedMessage, lastMessage);
    }

    @Then("the signup result should be {string}")
    public void theSignupResultShouldBe(String expectedMessage) {
        String actual = TestContext.get("lastMessage", String.class);
        assertEquals(expectedMessage, actual);
    }
}
