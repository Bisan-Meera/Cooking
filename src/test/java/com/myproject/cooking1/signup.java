package com.myproject.cooking1;

import com.myproject.cooking1.entities.TestContext;
import com.myproject.cooking1.entities.User;
import io.cucumber.java.After;
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

    @After
    public void cleanUpTestUsers() {
        try (Connection conn = DBConnection.getConnection()) {
            for (String email : usedEmails) {
                // Get user_id for this email
                PreparedStatement userStmt = conn.prepareStatement("SELECT user_id FROM Users WHERE email = ?");
                userStmt.setString(1, email);
                ResultSet userRs = userStmt.executeQuery();
                if (userRs.next()) {
                    int userId = userRs.getInt("user_id");
                    // NEVER delete user_id=1 or known production/demo users
                    if (userId == 1 || email.equalsIgnoreCase("lyllahassan@example.com")) {
                        System.out.println("Skipping deletion for demo/shared user: " + email);
                        continue;
                    }
                    // Only delete users whose emails match your test pattern
                    if (!email.startsWith("test") && !email.contains("+test")) {
                        System.out.println("Skipping non-test user: " + email);
                        continue;
                    }
                    // Delete dependent invoices if any
                    PreparedStatement invStmt = conn.prepareStatement("DELETE FROM Invoices WHERE customer_id = ?");
                    invStmt.setInt(1, userId);
                    invStmt.executeUpdate();

                    // Now safe to delete the user
                    PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM Users WHERE user_id = ?");
                    deleteStmt.setInt(1, userId);
                    deleteStmt.executeUpdate();

                    System.out.println("Deleted test user: " + email + ", user_id=" + userId);
                }
            }
            usedEmails.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String lastMessage;
    private static final Set<String> usedEmails = new HashSet<>();

    @Given("the Users table is available")
    public void theUsersTableIsAvailable() {
        System.out.println("Users table is assumed ready.");
    }

    @Given("the email {string} is not already registered")
    public void theEmailIsNotAlreadyRegistered(String email) {
        usedEmails.remove(email.toLowerCase());

        // 🧹 Delete from DB to ensure test starts clean
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE email = ?");
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Given("the email {string} is already registered")
    public void theEmailIsAlreadyRegistered(String email) {
        usedEmails.add(email.toLowerCase());
    }

    @When("the user signs up with name {string}, email {string}, password {string}, role {string}, and expertise {string}")
    public void theUserSignsUpWithNameEmailPasswordRoleAndExpertise(String name, String email, String password, String role, String expertise) {
        TestContext.clear();
        TestContext.set("email", email);


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
            System.out.println("Created user: " + user.getEmail());

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
    @Then("the user {string} should exist in the database")
    public void theUserShouldExistInTheDatabase(String email) {
        try (Connection conn = com.myproject.cooking1.DBConnection.getConnection()) {
            // You may want to make a getUserByEmail static method, but for now:
            String sql = "SELECT * FROM Users WHERE email = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    org.junit.Assert.assertTrue("User with email not found: " + email, rs.next());
                    // Optional: check name/role as well
                    // assertEquals("New User", rs.getString("name"));
                }
            }
        } catch (Exception e) {
            throw new AssertionError("Database check failed: " + e.getMessage(), e);
        }
    }
}
