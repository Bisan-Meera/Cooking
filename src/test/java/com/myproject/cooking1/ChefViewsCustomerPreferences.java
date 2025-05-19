package com.myproject.cooking1;

import com.myproject.cooking1.entities.ChefView;
import com.myproject.cooking1.entities.CustomerPreferences;
import com.myproject.cooking1.entities.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ChefViewsCustomerPreferences {

    @Given("a customer has placed an order")
    public void aCustomerHasPlacedAnOrder() {
        TestContext.set("customerId", 1); // assuming user_id = 1 for test
    }

    @Given("the customer has {string} preference and is allergic to {string}")
    public void theCustomerHasPreferenceAndIsAllergicTo(String preference, String allergy) {
        int customerId = TestContext.get("customerId", Integer.class);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO CustomerPreferences (user_id, dietary_preference, allergy) VALUES (?, ?, ?) " +
                    "ON CONFLICT (user_id) DO UPDATE SET dietary_preference = EXCLUDED.dietary_preference, allergy = EXCLUDED.allergy";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                stmt.setString(2, preference);
                stmt.setString(3, allergy);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @When("the chef opens the order details")
    public void theChefOpensTheOrderDetails() {
        int customerId = TestContext.get("customerId", Integer.class);

        ChefView view = new ChefView();
        view.loadPreferencesForCustomer(customerId);
        TestContext.set("chefView", view);
        TestContext.set("lastMessage", view.getDisplayedPreference());
    }

    @Then("the customer's preferences and allergies should be visible")
    public void theCustomerSPreferencesAndAllergiesShouldBeVisible() {
        String result = TestContext.get("lastMessage", String.class);
        System.out.println("Chef sees: " + result); // For debugging/logging in test
        assert result != null && !result.equals("No preferences specified") : "Expected preferences to be visible, but got: " + result;
    }

    @Given("multiple customers have placed orders")
    public void multipleCustomersHavePlacedOrders() {
        Map<Integer, CustomerPreferences> customerData = new HashMap<>();
        customerData.put(101, new CustomerPreferences("Vegetarian", "No Nuts"));
        customerData.put(102, new CustomerPreferences("Vegetarian", "Gluten"));
        customerData.put(103, new CustomerPreferences("Halal", "None"));

        try (Connection conn = DBConnection.getConnection()) {
            // Insert users first to satisfy foreign key constraint
            for (Integer userId : customerData.keySet()) {
                String userInsert = "INSERT INTO Users (user_id, name, email, password, role) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT (user_id) DO NOTHING";
                try (PreparedStatement stmt = conn.prepareStatement(userInsert)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, "TestUser" + userId);
                    stmt.setString(3, "user" + userId + "@example.com");
                    stmt.setString(4, "test123");  // Password (hashed or dummy)
                    stmt.setString(5, "customer");
                    stmt.executeUpdate();
                }
            }

            // Now insert their preferences
            for (Map.Entry<Integer, CustomerPreferences> entry : customerData.entrySet()) {
                int userId = entry.getKey();
                CustomerPreferences prefs = entry.getValue();

                String sql = "INSERT INTO CustomerPreferences (user_id, dietary_preference, allergy) VALUES (?, ?, ?) " +
                        "ON CONFLICT (user_id) DO UPDATE SET dietary_preference = EXCLUDED.dietary_preference, allergy = EXCLUDED.allergy";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, prefs.getDietaryPreference());
                    stmt.setString(3, prefs.getAllergy());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Store for later verification
        TestContext.set("multiCustomerData", customerData);
    }



    @When("the chef opens the order list")
    public void theChefOpensTheOrderList() {
        Map<Integer, CustomerPreferences> customerData = TestContext.get("multiCustomerData", Map.class);
        Map<Integer, String> customerDisplays = new HashMap<>();

        for (Integer customerId : customerData.keySet()) {
            ChefView view = new ChefView();
            view.loadPreferencesForCustomer(customerId);
            customerDisplays.put(customerId, view.getDisplayedPreference());
        }

        TestContext.set("customerDisplays", customerDisplays);
    }


    @Then("each order should display the corresponding customer's dietary preferences and allergies")
    public void eachOrderShouldDisplayTheCorrespondingCustomerSDietaryPreferencesAndAllergies() {
        Map<Integer, CustomerPreferences> expectedData = TestContext.get("multiCustomerData", Map.class);
        Map<Integer, String> actualDisplays = TestContext.get("customerDisplays", Map.class);

        for (Map.Entry<Integer, CustomerPreferences> entry : expectedData.entrySet()) {
            int customerId = entry.getKey();
            String expected = entry.getValue().toString();
            String actual = actualDisplays.get(customerId);

            System.out.println("Customer " + customerId + ":\nExpected: " + expected + "\nActual: " + actual + "\n");

            assert expected.equals(actual) : "Mismatch for customer " + customerId;
        }
    }


    @Given("a customer has placed an order without saving any preferences")
    public void aCustomerHasPlacedAnOrderWithoutSavingAnyPreferences() {
        int customerId = 3000;
        TestContext.set("customerId", customerId);

        try (Connection conn = DBConnection.getConnection()) {
            // 🧹 Step 1: DELETE existing preferences
            PreparedStatement deletePrefs = conn.prepareStatement(
                    "DELETE FROM CustomerPreferences WHERE user_id = ?"
            );
            deletePrefs.setInt(1, customerId);
            deletePrefs.executeUpdate();

            // 👤 Step 2: Ensure user exists
            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO Users (user_id, name, email, password, role) VALUES (?, ?, ?, ?, ?) " +
                            "ON CONFLICT DO NOTHING"
            );
            userStmt.setInt(1, customerId);
            userStmt.setString(2, "EmptyUser");
            userStmt.setString(3, "empty@example.com");
            userStmt.setString(4, "pass");
            userStmt.setString(5, "customer");
            userStmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed setup for preference-less user", e);
        }

        // 🧠 Step 3: Actually load into view (now guaranteed clean)
        ChefView view = new ChefView();
        view.loadPreferencesForCustomer(customerId);
        TestContext.set("chefView", view);
        TestContext.set("lastMessage", view.getDisplayedPreference());
    }


    @Then("the chef should see the message {string}")
    public void theChefShouldSeeTheMessage(String expectedMessage) {
        String actual = TestContext.get("lastMessage", String.class);
        System.out.println("EXPECTED: >" + expectedMessage + "<");
        System.out.println("ACTUAL  : >" + actual + "<");
        assertEquals(expectedMessage, actual);
    }


    @Given("a special customer ID triggers DB failure")
    public void aSpecialCustomerIdTriggersDbFailure() {
        TestContext.set("customerId", -999);
    }

}
