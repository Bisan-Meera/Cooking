package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceService {

    // Generate invoice for a specific order
    public static void generateInvoiceForOrder(int orderId, int customerId, double total) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO invoices (order_id, customer_id, total, issued_at) " +
                            "VALUES (?, ?, ?, CURRENT_TIMESTAMP)"
            );
            ps.setInt(1, orderId);
            ps.setInt(2, customerId);
            ps.setDouble(3, total);
            ps.executeUpdate();
            System.out.println("🧾 Invoice generated for Order ID: " + orderId);
        } catch (SQLException e) {
            System.out.println("❌ Failed to generate invoice.");
            e.printStackTrace();
        }
    }

    // Retrieve invoices for a customer
    public static List<String> getCustomerInvoices(int customerId) {
        List<String> invoices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT invoice_id, order_id, total, issued_at FROM invoices WHERE customer_id = ?"
            );
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                invoices.add(String.format("Invoice #%d | Order #%d | $%.2f | Date: %s",
                        rs.getInt("invoice_id"),
                        rs.getInt("order_id"),
                        rs.getDouble("total"),
                        rs.getTimestamp("issued_at")));
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to fetch customer invoices.");
            e.printStackTrace();
        }
        return invoices;
    }

    // Generate a daily summary report for admin
    public static void generateDailyRevenueReport() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                SELECT COUNT(*) AS total_orders, SUM(total) AS total_revenue
                FROM invoices
                WHERE DATE(issued_at) = CURRENT_DATE
            """;
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                System.out.println("\n📊 Daily Revenue Report:");
                System.out.println("Orders: " + rs.getInt("total_orders"));
                System.out.println("Revenue: $" + rs.getDouble("total_revenue"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to generate daily report.");
            e.printStackTrace();
        }
    }
}
