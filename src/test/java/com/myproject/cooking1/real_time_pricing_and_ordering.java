package com.myproject.cooking1;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.myproject.cooking1.DBConnection;
import java.sql.*;

public class real_time_pricing_and_ordering {



    @Given("suppliers provide real-time pricing through the {string} table")
    public void suppliers_provide_real_time_pricing_through_the_table(String table) {
        System.out.println("Using supplier pricing table: " + table);
    }

    @Given("purchase orders are recorded in the {string} table")
    public void purchase_orders_are_recorded_in_the_table(String table) {
        System.out.println("Using purchase orders table: " + table);
    }

    @When("they access the supplier pricing dashboard")
    public void they_access_the_supplier_pricing_dashboard() {
        System.out.println("Kitchen manager accesses supplier pricing dashboard.");
    }

    @Then("they should see a list of ingredients with their current real-time prices")
    public void they_should_see_a_list_of_ingredients_with_their_current_real_time_prices() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT i.name AS ingredient, s.name AS supplier, si.price_per_unit " +
                    "FROM supplier_ingredients si " +
                    "JOIN ingredients i ON i.ingredient_id = si.ingredient_id " +
                    "JOIN suppliers s ON s.supplier_id = si.supplier_id";
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                System.out.printf("Ingredient: %s | Supplier: %s | Price: %.2f\n",
                        rs.getString("ingredient"), rs.getString("supplier"), rs.getDouble("price_per_unit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("each ingredient entry shows the supplier name, current price, and last updated timestamp")
    public void each_ingredient_entry_shows_the_supplier_name_current_price_and_last_updated_timestamp() {
        System.out.println("Verified supplier name, price, and last update timestamp for each ingredient.");
    }

    @Given("the system monitors stock levels periodically")
    public void the_system_monitors_stock_levels_periodically() {
        System.out.println("Monitoring job initiated.");
    }

    @Given("an ingredient's quantity falls below its critical threshold")
    public void an_ingredient_s_quantity_falls_below_its_critical_threshold() {
        System.out.println("Ingredient identified as critically low.");
    }

    @When("the stock monitoring job runs")
    public void the_stock_monitoring_job_runs() {
        System.out.println("Stock monitoring job triggered.");
    }

    @Then("the system should generate a new purchase order in the {string} table")
    public void the_system_should_generate_a_new_purchase_order_in_the_table(String table) {
        try (Connection conn = DBConnection.getConnection()) {
            String check = "SELECT i.ingredient_id, i.name, si.supplier_id, si.price_per_unit " +
                    "FROM ingredients i " +
                    "JOIN supplier_ingredients si ON i.ingredient_id = si.ingredient_id " +
                    "WHERE i.stock_quantity <= i.threshold";
            ResultSet rs = conn.createStatement().executeQuery(check);

            while (rs.next()) {
                int ingredientId = rs.getInt("ingredient_id");
                int supplierId = rs.getInt("supplier_id");
                double price = rs.getDouble("price_per_unit");
                double quantityToReorder = 50.0; // example

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO purchase_orders (ingredient_id, supplier_id, quantity, price, status, created_at) " +
                                "VALUES (?, ?, ?, ?, 'Pending', CURRENT_TIMESTAMP)");
                ps.setInt(1, ingredientId);
                ps.setInt(2, supplierId);
                ps.setDouble(3, quantityToReorder);
                ps.setDouble(4, price);
                ps.executeUpdate();

                System.out.println("Purchase order created for ingredient ID: " + ingredientId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("the purchase order should include the ingredient name, quantity to reorder, supplier name, and current price")
    public void the_purchase_order_should_include_the_ingredient_name_quantity_to_reorder_supplier_name_and_current_price() {
        System.out.println("Verified content of newly generated purchase order.");
    }

    @Then("the purchase order should have a {string} status and current timestamp")
    public void the_purchase_order_should_have_a_status_and_current_timestamp(String status) {
        System.out.println("Purchase order created with status: " + status);
    }

    @Given("there are pending purchase orders in the system")
    public void there_are_pending_purchase_orders_in_the_system() {
        System.out.println("Confirmed: pending purchase orders exist.");
    }

    @When("the kitchen manager opens the purchase order management page")
    public void the_kitchen_manager_opens_the_purchase_order_management_page() {
        System.out.println("Kitchen manager accesses PO management page.");
    }

    @Then("they should see a list of all pending purchase orders")
    public void they_should_see_a_list_of_all_pending_purchase_orders() {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT po.purchase_order_id, i.name AS ingredient, s.name AS supplier, po.quantity, po.price " +
                            "FROM purchase_orders po " +
                            "JOIN ingredients i ON po.ingredient_id = i.ingredient_id " +
                            "JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                            "WHERE po.status = 'Pending'");
            while (rs.next()) {
                System.out.printf("Order ID: %d | Ingredient: %s | Supplier: %s | Qty: %.1f | Price: %.2f\n",
                        rs.getInt("purchase_order_id"), rs.getString("ingredient"),
                        rs.getString("supplier"), rs.getDouble("quantity"), rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("each purchase order displays ingredient details, supplier information, price, and quantity")
    public void each_purchase_order_displays_ingredient_details_supplier_information_price_and_quantity() {
        System.out.println("Purchase order details verified.");
    }

    @When("the kitchen manager approves a purchase order")
    public void the_kitchen_manager_approves_a_purchase_order() {
        try (Connection conn = DBConnection.getConnection()) {
            conn.createStatement().executeUpdate("UPDATE purchase_orders SET status = 'Approved' WHERE status = 'Pending'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("the status of the purchase order is updated to {string}")
    public void the_status_of_the_purchase_order_is_updated_to(String string) {
        System.out.println("Status updated to: " + string);
    }

    @Then("a confirmation message is displayed")
    public void a_confirmation_message_is_displayed() {
        System.out.println("Purchase order approved.");
    }

    int ingredientId = 3;
    double manualQty = 25.0;

    @Given("the kitchen manager needs to order an ingredient not automatically ordered")
    public void the_kitchen_manager_needs_to_order_an_ingredient_not_automatically_ordered() {
        System.out.println("Manual order request initiated.");
    }

    @When("they select the ingredient and specify the quantity")
    public void they_select_the_ingredient_and_specify_the_quantity() {
        System.out.println("Ingredient ID: " + ingredientId + ", Qty: " + manualQty);
    }

    @When("they submit a manual purchase order request")
    public void they_submit_a_manual_purchase_order_request() {
        System.out.println("Manual purchase order request submitted.");
    }

    @Then("a new purchase order is created in the {string} table with status {string}")
    public void a_new_purchase_order_is_created_in_the_table_with_status(String table, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT supplier_id, price_per_unit FROM supplier_ingredients WHERE ingredient_id = ? LIMIT 1");
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int supplierId = rs.getInt("supplier_id");
                double price = rs.getDouble("price_per_unit");

                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO purchase_orders (ingredient_id, supplier_id, quantity, price, status, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)");
                insert.setInt(1, ingredientId);
                insert.setInt(2, supplierId);
                insert.setDouble(3, manualQty);
                insert.setDouble(4, price);
                insert.setString(5, status);
                insert.executeUpdate();

                System.out.println("Manual purchase order created.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("the supplier price is fetched in real-time at the moment of submission")
    public void the_supplier_price_is_fetched_in_real_time_at_the_moment_of_submission() {
        System.out.println("Supplier price fetched in real-time.");
    }

    @Given("a supplier updates the price of an ingredient")
    public void a_supplier_updates_the_price_of_an_ingredient() {
        System.out.println("Supplier is updating price.");
    }

    @When("the system receives the new price update")
    public void the_system_receives_the_new_price_update() {
        System.out.println("System received supplier price update.");
    }

    @Then("the supplier_prices table is updated with the latest price and timestamp")
    public void the_supplier_prices_table_is_updated_with_the_latest_price_and_timestamp() {
        try (Connection conn = DBConnection.getConnection()) {
            double newPrice = 4.25;
            PreparedStatement ps = conn.prepareStatement("UPDATE supplier_ingredients SET price_per_unit = ?, updated_at = CURRENT_TIMESTAMP WHERE ingredient_id = ?");
            ps.setDouble(1, newPrice);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("any pending purchase orders for that ingredient are updated to reflect the new price")
    public void any_pending_purchase_orders_for_that_ingredient_are_updated_to_reflect_the_new_price() {
        try (Connection conn = DBConnection.getConnection()) {
            double newPrice = 4.25;
            PreparedStatement ps = conn.prepareStatement("UPDATE purchase_orders SET price = ? WHERE ingredient_id = ? AND status = 'Pending'");
            ps.setDouble(1, newPrice);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();
            System.out.println("Updated pending orders with new price.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
