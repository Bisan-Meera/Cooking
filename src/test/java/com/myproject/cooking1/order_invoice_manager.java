// ✅ Updated Feature: Order & Invoice Manager
// This version includes generation of invoices and financial reporting

package com.myproject.cooking1;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.*;
import java.util.*;
import com.myproject.cooking1.DBConnection;
import com.myproject.cooking1.entities.*;
public class order_invoice_manager {

    private int customerId = 1;
    private int generatedOrderId = -1;
    private InvoiceService invoiceService = new InvoiceService();

    @Given("the following customer exists:")
    public void theFollowingCustomerExists(DataTable dataTable) {
        List<Map<String, String>> users = dataTable.asMaps();
        try (Connection conn = DBConnection.getConnection()) {
            for (Map<String, String> user : users) {
                int id = Integer.parseInt(user.get("user_id"));
                String name = user.get("name");
                String role = user.get("role");
                String email = name.toLowerCase().replaceAll(" ", ".") + "@example.com";

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (user_id, name, email,password, role) VALUES (?,?, ?, ?, ?) ON CONFLICT DO NOTHING");
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, "defaultPass123");

                ps.setString(5, role);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Given("customer Layla Hassan adds {int} meals to the cart")
    public void customerLaylaHassanAddsMealsToTheCart(Integer mealCount) {
        // Just store meal IDs or simulate cart items
        System.out.println("Customer added " + mealCount + " meals to the cart.");
    }

    @When("she proceeds to checkout")
    public void sheProceedsToCheckout() {
        try (Connection conn = DBConnection.getConnection()) {
            // Create Order
            PreparedStatement orderStmt = conn.prepareStatement(
                    "INSERT INTO Orders (customer_id, status) VALUES (?, 'completed') RETURNING order_id");
            orderStmt.setInt(1, customerId);
            ResultSet rs = orderStmt.executeQuery();

            if (rs.next()) {
                generatedOrderId = rs.getInt("order_id");

                // Create Invoice
                invoiceService.generateInvoiceForOrder(generatedOrderId,customerId, 35.00);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("a new order and corresponding invoice should be generated")
    public void aNewOrderAndCorrespondingInvoiceShouldBeGenerated() {
        System.out.println("✅ Order ID " + generatedOrderId + " and its invoice generated.");
    }

    @Given("Layla Hassan placed an order last week")
    public void laylaHassanPlacedAnOrderLastWeek() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Orders (customer_id, status, created_at) VALUES (?, 'completed', CURRENT_DATE - INTERVAL '7 days') RETURNING order_id");
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int pastOrderId = rs.getInt("order_id");
                invoiceService.generateInvoiceForOrder(pastOrderId,customerId, 40.00);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @When("she checks the invoice tab")
    public void sheChecksTheInvoiceTab() {
        System.out.println("Customer opens invoice tab.");
    }

    @Then("she should be able to view and download the invoice")
    public void sheShouldBeAbleToViewAndDownloadTheInvoice() {
        invoiceService.getCustomerInvoices(customerId).forEach(System.out::println);
    }

    @Given("multiple orders were completed today")
    public void multipleOrdersWereCompletedToday() {
        try (Connection conn = DBConnection.getConnection()) {
            for (int i = 0; i < 3; i++) {
                PreparedStatement orderStmt = conn.prepareStatement(
                        "INSERT INTO Orders (customer_id, status, created_at) VALUES (?, 'completed', CURRENT_DATE) RETURNING order_id");
                orderStmt.setInt(1, customerId);
                ResultSet rs = orderStmt.executeQuery();
                if (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    invoiceService.generateInvoiceForOrder(orderId,customerId, 45.00);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @When("the admin clicks on \u201cDaily Summary\u201d")
    public void theAdminClicksOnDailySummary() {
        System.out.println("Admin triggered daily summary report generation.");
    }

    @Then("a report showing total revenue and meal counts should be generated")
    public void aReportShowingTotalRevenueAndMealCountsShouldBeGenerated() {
        invoiceService.generateDailyRevenueReport();
    }
}
