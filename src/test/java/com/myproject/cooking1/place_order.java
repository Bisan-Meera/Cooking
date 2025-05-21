package com.myproject.cooking1;

import com.myproject.cooking1.entities.OrderService;
import com.myproject.cooking1.DBConnection;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class place_order {

    private final List<String> selectedMeals = new ArrayList<>();
    private int customerId = 1; // assume a logged-in customer
    private int orderId;

    @Given("the system has predefined meals")
    public void theSystemHasPredefinedMeals() {
        List<String> expectedMeals = List.of("Chicken Biryani", "Grilled Salmon", "Vegetarian Lasagna");
        List<String> foundMeals = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            for (String meal : expectedMeals) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT name FROM Meals WHERE name ILIKE ?"
                );
                stmt.setString(1, meal);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    foundMeals.add(rs.getString("name"));
                    System.out.println("🟢 Found meal: " + rs.getString("name"));
                } else {
                    System.out.println("❌ Missing meal: " + meal);
                }
                stmt.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Database error while validating meals", e);
        }

        assertEquals("Not all required meals are present in the database",
                expectedMeals.size(), foundMeals.size());
    }




    @When("the customer selects the following meal:")
    public void theCustomerSelectsTheFollowingMeal(DataTable dataTable) {
        selectedMeals.clear();
        selectedMeals.addAll(dataTable.asList());
    }

    @When("the customer selects the following meals:")
    public void theCustomerSelectsTheFollowingMeals(DataTable dataTable) {
        selectedMeals.clear();
        selectedMeals.addAll(dataTable.asList());
    }

    @Then("an order should be added to the system")
    public void anOrderShouldBeAddedToTheSystem() throws SQLException {
        OrderService service = new OrderService();
        orderId = service.createOrder(customerId, selectedMeals);
        assertTrue("Order was not created", orderId > 0);
    }

    @Then("a task should be created for kitchen management")
    public void aTaskShouldBeCreatedForKitchenManagement() throws SQLException {
        List<String> taskList = OrderService.getTasksByOrderId(orderId);
        assertEquals("Expected 1 task per order", 1, taskList.size());
    }
    @Then("the system should handle the order according to ingredient stock levels")
    public void theSystemShouldHandleOrderBasedOnStock() {
        try {
            OrderService service = new OrderService();
            orderId = service.createOrder(customerId, selectedMeals);
            System.out.println("✅ Order placed successfully with order ID: " + orderId);
            assertTrue("Order should be created when stock is sufficient", orderId > 0);

            List<String> taskList = OrderService.getTasksByOrderId(orderId);
            assertEquals("Expected 1 task per order", 1, taskList.size());

        } catch (RuntimeException e) {
            System.out.println("❌ Order failed as expected: " + e.getMessage());
            assertTrue("Order failed due to low stock or DB issue", e.getMessage().startsWith("Cannot order")
                    || e.getMessage().startsWith("Failed to create order")
                    || e.getMessage().startsWith("Meal not found"));

        } catch (SQLException e) {
            throw new RuntimeException("Unexpected SQL error", e);
        } finally {
            // Always reset
            OrderService.FORCE_DB_ERROR = false;
        }
    }


    @And("a database error is simulated")
    public void aDatabaseErrorIsSimulated() {
        OrderService.FORCE_DB_ERROR = true;
    }





}
