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
}
