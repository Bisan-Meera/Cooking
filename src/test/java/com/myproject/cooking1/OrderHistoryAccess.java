package com.myproject.cooking1;

import com.myproject.cooking1.entities.AdminService;
import com.myproject.cooking1.entities.OrderService;
import io.cucumber.java.en.*;
import static org.junit.Assert.*;


import java.sql.SQLException;
import java.util.*;

public class OrderHistoryAccess {

    OrderService orderService = new OrderService();
    AdminService adminService = new AdminService();

    List<Map<String, String>> customerOrders;
    Map<Integer, List<String>> allCustomerOrders;

    public OrderHistoryAccess() throws SQLException {
    }

    @Given("the customer {string} with user_id {int} has ordered {string}")
    public void theCustomerWithUserIdHasOrdered(String name, Integer userId, String mealName) {
        customerOrders = orderService.getCustomerOrderHistory(userId);
        boolean mealFound = customerOrders.stream()
                .anyMatch(meal -> meal.get("name").equalsIgnoreCase(mealName));
        assertTrue("Expected meal not found in customer history.", mealFound);
    }

    @When("she logs in and goes to her order history page")
    public void sheLogsInAndGoesToHerOrderHistoryPage() {
        // already fetched in @Given
    }

    @Then("she should see {string} listed with price and description")
    public void sheShouldSeeListedWithPriceAndDescription(String mealName) {
        Map<String, String> meal = customerOrders.stream()
                .filter(m -> m.get("name").equalsIgnoreCase(mealName))
                .findFirst().orElse(null);
        assertNotNull("Meal not found in order history.", meal);
        System.out.println("Meal: " + meal.get("name") + " - $" + meal.get("price") +
                "\nDescription: " + meal.get("description"));
    }

    @Given("the customer {string} with user_id {int} has no past orders")
    public void theCustomerWithUserIdHasNoPastOrders(String name, Integer userId) {
        customerOrders = orderService.getCustomerOrderHistory(userId);
        assertTrue("Customer should have no past orders.", customerOrders.isEmpty());
    }

    @When("he logs in and goes to the order history page")
    public void heLogsInAndGoesToTheOrderHistoryPage() {
        // Already fetched in @Given
    }

    @Then("he should see a message saying {string}")
    public void heShouldSeeAMessageSaying(String message) {
        if (customerOrders.isEmpty()) {
            System.out.println(message);
            assertTrue(true);
        } else {
            fail("Customer has orders but shouldn't.");
        }
    }

    @Given("the chef {string} is logged in")
    public void theChefIsLoggedIn(String name) {
        System.out.println("Chef " + name + " logged in.");
    }

    @Given("customer {string} has placed multiple orders")
    public void customerHasPlacedMultipleOrders(String customerName) {
        customerOrders = orderService.getCustomerOrderHistory(1); // assuming 1 for test
        assertTrue("Customer should have multiple orders.", customerOrders.size() > 1);
    }

    @When("the chef selects {string} from the customer list")
    public void theChefSelectsFromTheCustomerList(String customerName) {
        // Assuming customer_id = 1 for demo
        customerOrders = orderService.getCustomerOrderHistory(1);
    }

    @Then("he should see all meals she has ordered in the past")
    public void heShouldSeeAllMealsSheHasOrderedInThePast() {
        customerOrders.forEach(order -> System.out.println("Meal: " + order.get("name")));
        assertFalse(customerOrders.isEmpty());
    }

    @Given("the admin {string} is logged in")
    public void theAdminIsLoggedIn(String name) {
        System.out.println("Admin " + name + " logged in.");
    }

    @When("she accesses the system order analytics dashboard")
    public void sheAccessesTheSystemOrderAnalyticsDashboard() throws SQLException {
        allCustomerOrders = adminService.getAllCustomerOrders();
    }


    @Then("she should be able to retrieve all orders placed by all customers")
    public void sheShouldBeAbleToRetrieveAllOrdersPlacedByAllCustomers() {
        allCustomerOrders.forEach((id, meals) -> {
            System.out.println("Customer " + id + ": " + meals);
        });
        assertFalse(allCustomerOrders.isEmpty());
    }
}
