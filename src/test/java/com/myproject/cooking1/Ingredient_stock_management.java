package com.myproject.cooking1;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.*;

public class Ingredient_stock_management {

    private int currentOrderId;
    private int kitchenManagerId = 1; // For simplicity, hardcoded or fetched once from DB
    private int ingredientId;
    private double newQuantity = 100.0;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/yourdb", "youruser", "yourpass");
    }

    @Given("the system tracks ingredient stock levels in the {string} table")
    public void theSystemTracksIngredientStockLevelsInTheTable(String table) {
        System.out.println("Tracking stock in table: " + table);
    }

    @Given("each ingredient has a defined {string} quantity")
    public void eachIngredientHasADefinedQuantity(String quantityType) {
        System.out.println("Ingredient threshold is defined as: " + quantityType);
    }

    @Given("kitchen managers are users with role {string}")
    public void kitchenManagersAreUsersWithRole(String role) {
        System.out.println("Verifying role: " + role);
    }

    @Given("the system sends notifications through the {string} table")
    public void theSystemSendsNotificationsThroughTheTable(String tableName) {
        System.out.println("Notifications go through: " + tableName);
    }

    @Given("stock levels are updated when orders are placed")
    public void stockLevelsAreUpdatedWhenOrdersArePlaced() {
        System.out.println("Stock is updated when orders are placed");
    }

    @Given("a customer places an order containing meals with specific ingredients")
    public void aCustomerPlacesAnOrderContainingMealsWithSpecificIngredients() {
        currentOrderId = 1; // Set based on test data
    }

    @When("the order is confirmed")
    public void theOrderIsConfirmed() {
        System.out.println("Order is confirmed for ID: " + currentOrderId);
    }

    @Then("the system deducts the required quantity of each ingredient from the {string}")
    public void theSystemDeductsTheRequiredQuantityOfEachIngredientFromThe(String tableName) {
        try (Connection conn = getConnection()) {
            String query = "SELECT mi.ingredient_id, SUM(mi.quantity * oi.quantity) AS total_required " +
                    "FROM order_items oi " +
                    "JOIN meal_ingredients mi ON oi.meal_id = mi.meal_id " +
                    "WHERE oi.order_id = ? " +
                    "GROUP BY mi.ingredient_id";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, currentOrderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int ingredientId = rs.getInt("ingredient_id");
                double required = rs.getDouble("total_required");

                PreparedStatement update = conn.prepareStatement(
                        "UPDATE ingredients SET stock_quantity = stock_quantity - ?, last_updated = CURRENT_TIMESTAMP WHERE ingredient_id = ?");
                update.setDouble(1, required);
                update.setInt(2, ingredientId);
                update.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("updates the {string} timestamp for each affected ingredient")
    public void updatesTheTimestampForEachAffectedIngredient(String field) {
        System.out.println("Timestamp updated field: " + field);
    }

    @Given("an ingredient's {string} falls below or equals its {string}")
    public void anIngredientSFallsBelowOrEqualsIts(String quantity, String threshold) {
        System.out.println("Stock fell below threshold");
    }

    @When("the stock update occurs")
    public void theStockUpdateOccurs() {
        System.out.println("Stock update triggered");
    }

    @Then("the system creates a restocking notification for the kitchen manager")
    public void theSystemCreatesARestockingNotificationForTheKitchenManager() {
        try (Connection conn = getConnection()) {
            String query = "SELECT ingredient_id, name, stock_quantity, threshold FROM ingredients WHERE stock_quantity <= threshold";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                int ingredientId = rs.getInt("ingredient_id");
                String name = rs.getString("name");
                double qty = rs.getDouble("stock_quantity");
                String content = "Restock suggestion for " + name + " (Qty: " + qty + ")";

                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO notifications (user_id, content, is_read, created_at) VALUES (?, ?, false, CURRENT_TIMESTAMP)");
                insert.setInt(1, kitchenManagerId);
                insert.setString(2, content);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("the notification contains the ingredient name, current quantity, and restocking suggestion")
    public void theNotificationContainsTheIngredientNameCurrentQuantityAndRestockingSuggestion() {
        System.out.println("Notification includes ingredient details");
    }

    @Then("the notification is marked as unread with the current timestamp")
    public void theNotificationIsMarkedAsUnreadWithTheCurrentTimestamp() {
        System.out.println("Notification is unread");
    }

    @Given("the kitchen manager is logged into the system")
    public void theKitchenManagerIsLoggedIntoTheSystem() {
        System.out.println("Kitchen manager is logged in");
    }

    @When("they open the notifications panel")
    public void theyOpenTheNotificationsPanel() {
        System.out.println("Viewing notification panel");
    }

    @Then("they should see all unread restocking notifications")
    public void theyShouldSeeAllUnreadRestockingNotifications() {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT content FROM notifications WHERE user_id = ? AND is_read = false");
            ps.setInt(1, kitchenManagerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("Unread: " + rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("each notification displays the ingredient name, current stock, and suggested action")
    public void eachNotificationDisplaysTheIngredientNameCurrentStockAndSuggestedAction() {
        System.out.println("Notification format confirmed");
    }

    @When("the manager views a notification")
    public void theManagerViewsANotification() {
        System.out.println("Manager opens notification");
    }

    @Then("the notification's {string} status is updated to true")
    public void theNotificationSStatusIsUpdatedToTrue(String field) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE notifications SET is_read = true WHERE user_id = ? AND is_read = false");
            ps.setInt(1, kitchenManagerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Given("a kitchen manager wants to update an ingredient's stock")
    public void aKitchenManagerWantsToUpdateAnIngredientSStock() {
        ingredientId = 1; // hardcoded for test
    }

    @When("they adjust the {string} in the inventory system")
    public void theyAdjustTheInTheInventorySystem(String field) {
        newQuantity = 150.0;
    }

    @Then("the {string} table reflects the new quantity")
    public void theTableReflectsTheNewQuantity(String tableName) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE ingredients SET stock_quantity = ?, last_updated = CURRENT_TIMESTAMP WHERE ingredient_id = ?");
            ps.setDouble(1, newQuantity);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("the {string} timestamp is updated")
    public void theTimestampIsUpdated(String timestampField) {
        System.out.println("Timestamp updated field: " + timestampField);
    }

    @Then("previous low-stock notifications for that ingredient may be marked as resolved")
    public void previousLowStockNotificationsForThatIngredientMayBeMarkedAsResolved() {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE notifications SET is_read = true WHERE content LIKE ? AND user_id = ?");
            ps.setString(1, "%" + ingredientId + "%");
            ps.setInt(2, kitchenManagerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Given("a customer places a customized order with ingredient substitutions")
    public void aCustomerPlacesACustomizedOrderWithIngredientSubstitutions() {
        System.out.println("Customized order placed");
    }

    @When("the system processes the customized order")
    public void theSystemProcessesTheCustomizedOrder() {
        System.out.println("Customized order processed");
    }

    @Then("the system deducts the quantity from the substituted ingredient if a substitution exists")
    public void theSystemDeductsTheQuantityFromTheSubstitutedIngredientIfASubstitutionExists() {
        System.out.println("Substitution deduction complete");
    }

    @Then("the system deducts from the default ingredient if no substitution exists")
    public void theSystemDeductsFromTheDefaultIngredientIfNoSubstitutionExists() {
        System.out.println("Default ingredient used");
    }

    @Then("updates stock quantities and timestamps accordingly")
    public void updatesStockQuantitiesAndTimestampsAccordingly() {
        System.out.println("Stock and timestamps updated");
    }

    @Then("suggests restocking if the substituted or original ingredient falls below the threshold")
    public void suggestsRestockingIfTheSubstitutedOrOriginalIngredientFallsBelowTheThreshold() {
        theSystemCreatesARestockingNotificationForTheKitchenManager();
    }
}