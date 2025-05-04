package com.myproject.cooking1;

import com.myproject.cooking1.entities.CustomOrderService;
import com.myproject.cooking1.entities.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class custom_meal_creator {
    @Given("the Ingredients table has current stock levels")
    public void theIngredientsTableHasCurrentStockLevels() {
        try (Connection conn = DBConnection.getConnection()) {
            String[] sampleIngredients = {"chicken", "rice", "broccoli"};

            for (String name : sampleIngredients) {
                // Step 1: Try to insert only if it doesn't exist
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO Ingredients (name, stock_quantity, unit, threshold) " +
                                "VALUES (?, ?, ?, ?) ON CONFLICT (name) DO NOTHING"
                );
                insertStmt.setString(1, name);
                insertStmt.setDouble(2, 10.0);
                insertStmt.setString(3, "kg");
                insertStmt.setDouble(4, 0.05);
                insertStmt.executeUpdate();
                insertStmt.close();

                // Step 2: Verify it now exists and has stock
                PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT stock_quantity FROM Ingredients WHERE name ILIKE ?"
                );
                checkStmt.setString(1, name);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    double qty = rs.getDouble("stock_quantity");
                    if (qty <= 0) {
                        throw new AssertionError("⚠ Ingredient '" + name + "' has no stock (quantity = 0)");
                    }
                } else {
                    throw new AssertionError("❌ Ingredient '" + name + "' not found in table");
                }
                checkStmt.close();
            }

            System.out.println("✅ Ingredients table has current stock levels and nothing was overwritten.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to validate ingredients table", e);
        }
    }

    @Given("the user is logged in as a customer")
    public void theUserIsLoggedInAsACustomer() {
        int userId = 1; // Existing customer ID
        TestContext.set("userId", userId);

        System.out.println("🟢 Simulated login for customer with user_id = " + userId);
    }

    @Given("a customer selects chicken, rice, and broccoli")
    public void aCustomerSelectsChickenRiceAndBroccoli() {
        List<String> ingredients = List.of("chicken", "rice", "broccoli");
        TestContext.set("selectedIngredients", ingredients);

        try (Connection conn = DBConnection.getConnection()) {
            Map<String, Double> initialStock = new HashMap<>();
            for (String name : ingredients) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT stock_quantity FROM Ingredients WHERE name ILIKE ?"
                );
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    initialStock.put(name, rs.getDouble("stock_quantity"));
                }
                stmt.close();
            }
            TestContext.set("initialStock", initialStock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch initial stock", e);
        }
    }

    @When("they submit the custom meal request")
    public void theySubmitTheCustomMealRequest() {
        int userId = TestContext.get("userId", Integer.class);
        List<String> ingredients = TestContext.get("selectedIngredients", List.class);

        boolean success = CustomOrderService.submitCustomMeal(userId, ingredients);
        TestContext.set("orderSuccess", success);
    }

    @Then("the system should accept and save the customized order")
    public void theSystemShouldAcceptAndSaveTheCustomizedOrder() {
        boolean result = TestContext.get("orderSuccess", Boolean.class);
        assertTrue("Expected custom meal to be accepted and saved", result);
    }

    @Then("deduct the used ingredient quantities from stock")
    public void deductTheUsedIngredientQuantitiesFromStock() {
        List<String> ingredients = TestContext.get("selectedIngredients", List.class);
        Map<String, Double> initialStock = TestContext.get("initialStock", Map.class);

        try (Connection conn = DBConnection.getConnection()) {
            for (String name : ingredients) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT stock_quantity FROM Ingredients WHERE name ILIKE ?"
                );
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double after = rs.getDouble("stock_quantity");
                    double before = initialStock.get(name);
                    double usedQty = 0.10;

                    System.out.println(name + " before: " + before + ", after: " + after);
                    assertEquals("Stock not deducted correctly for " + name, before - usedQty, after, 0.01);
                } else {
                    throw new AssertionError("Ingredient not found: " + name);
                }
                stmt.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify stock deduction", e);
        }
    }

    @Given("the ingredient {string} is currently out of stock")
    public void theIngredientIsCurrentlyOutOfStock(String ingredient) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Ingredients SET stock_quantity = 0 WHERE name ILIKE ?"
            );
            stmt.setString(1, ingredient);
            stmt.executeUpdate();
            stmt.close();
            TestContext.set("outOfStockIngredient", ingredient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark ingredient out of stock", e);
        }
    }

    @When("a customer tries to add {string} to their custom meal")
    public void aCustomerTriesToAddToTheirCustomMeal(String ingredient) {
        int userId = TestContext.get("userId", Integer.class);
        List<String> ingredients = List.of(ingredient);

        try {
            boolean success = CustomOrderService.submitCustomMeal(userId, ingredients);
            TestContext.set("orderSuccess", success);
            TestContext.set("lastMessage", null);
        } catch (Exception e) {
            TestContext.set("orderSuccess", false);
            TestContext.set("lastMessage", e.getMessage());
        }
    }

    @Given("a customer is on the custom meal creator page")
    public void aCustomerIsOnTheCustomMealCreatorPage() {
        int userId = 1;
        TestContext.set("userId", userId);
        System.out.println("🧾 Customer " + userId + " is on the custom meal creator page.");
    }


    @When("they submit the form without selecting any ingredients")
    public void theySubmitTheFormWithoutSelectingAnyIngredients() {
        int userId = TestContext.get("userId", Integer.class);
        List<String> ingredients = List.of();

        try {
            boolean success = CustomOrderService.submitCustomMeal(userId, ingredients);
            TestContext.set("orderSuccess", success);
            TestContext.set("lastMessage", null);
        } catch (Exception e) {
            TestContext.set("orderSuccess", false);
            TestContext.set("lastMessage", e.getMessage());
        }
    }
    @Then("the system should notify {string}")
    public void theSystemShouldNotify(String expectedMessage) {
        String actualMessage = TestContext.get("lastMessage", String.class);
        assertEquals(expectedMessage, actualMessage);
    }


    @Given("a customer selects chicken and rice")
    public void aCustomerSelectsChickenAndRice() {
        List<String> ingredients = List.of("chicken", "rice");
        TestContext.set("selectedIngredients", ingredients);
    }

    @When("a database error occurs during the save")
    public void aDatabaseErrorOccursDuringTheSave() {
        int userId = TestContext.get("userId", Integer.class);
        List<String> ingredients = TestContext.get("selectedIngredients", List.class);

        try {
            boolean success = CustomOrderService.submitCustomMeal(userId, ingredients);
            TestContext.set("orderSuccess", success);
        } catch (Exception e) {
            TestContext.set("orderSuccess", false);
            TestContext.set("lastMessage", "Failed to save custom meal due to system error");
        }
    }
}