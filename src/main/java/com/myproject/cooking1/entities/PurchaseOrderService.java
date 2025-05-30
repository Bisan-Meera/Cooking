package com.myproject.cooking1.entities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.myproject.cooking1.DBConnection;

public class PurchaseOrderService {

    public void createAutoPurchaseOrders() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT i.ingredient_id, i.name, si.supplier_id, si.price_per_unit " +
                             "FROM ingredients i " +
                             "JOIN supplier_ingredients si ON i.ingredient_id = si.ingredient_id " +
                             "WHERE i.stock_quantity <= i.threshold")) {

            while (rs.next()) {
                int ingredientId = rs.getInt("ingredient_id");
                int supplierId = rs.getInt("supplier_id");
                double price = rs.getDouble("price_per_unit");
                double quantityToReorder = 50.0;

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO purchase_orders (ingredient_id, supplier_id, quantity, price, status, created_at) " +
                                "VALUES (?, ?, ?, ?, 'Pending', CURRENT_TIMESTAMP)")) {
                    ps.setInt(1, ingredientId);
                    ps.setInt(2, supplierId);
                    ps.setDouble(3, quantityToReorder);
                    ps.setDouble(4, price);
                    ps.executeUpdate();
                }
                System.out.println("Auto order created for ingredient ID: " + ingredientId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void approveAllPendingOrders() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            int updated = stmt.executeUpdate("UPDATE purchase_orders SET status = 'Approved' WHERE status = 'Pending'");
            System.out.println(updated + " pending orders approved.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createManualOrder(int ingredientId, double quantity) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT supplier_id, price_per_unit FROM supplier_ingredients WHERE ingredient_id = ? LIMIT 1")) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int supplierId = rs.getInt("supplier_id");
                    double price = rs.getDouble("price_per_unit");

                    try (PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO purchase_orders (ingredient_id, supplier_id, quantity, price, status, created_at) " +
                                    "VALUES (?, ?, ?, ?, 'Pending', CURRENT_TIMESTAMP)")) {
                        insert.setInt(1, ingredientId);
                        insert.setInt(2, supplierId);
                        insert.setDouble(3, quantity);
                        insert.setDouble(4, price);
                        insert.executeUpdate();
                    }

                    System.out.println("Manual order created.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> listPendingOrders() {
        List<String> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT po.purchase_order_id, i.name AS ingredient, s.name AS supplier, po.quantity, po.price " +
                             "FROM purchase_orders po " +
                             "JOIN ingredients i ON po.ingredient_id = i.ingredient_id " +
                             "JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                             "WHERE po.status = 'Pending'")) {
            while (rs.next()) {
                String summary = String.format("Order ID: %d | Ingredient: %s | Supplier: %s | Qty: %.1f | Price: %.2f",
                        rs.getInt("purchase_order_id"),
                        rs.getString("ingredient"),
                        rs.getString("supplier"),
                        rs.getDouble("quantity"),
                        rs.getDouble("price"));
                orders.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void openOrderSubMenu(Scanner scanner) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Purchase Order Management ---");
            System.out.println("1. View Pending Orders");
            System.out.println("2. Create Manual Order");
            System.out.println("3. Generate Auto Orders");
            System.out.println("4. Approve All Pending Orders");
            System.out.println("5. Back to Kitchen Manager Page");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    List<String> pending = listPendingOrders();
                    if (pending.isEmpty()) {
                        System.out.println("No pending orders.");
                    } else {
                        pending.forEach(System.out::println);
                    }
                    break;
                case "2":
                    try {
                        System.out.print("Enter Ingredient ID: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Enter Quantity: ");
                        double qty = Double.parseDouble(scanner.nextLine().trim());
                        createManualOrder(id, qty);
                    } catch (Exception e) {
                        System.out.println("Invalid input. Try again.");
                    }
                    break;
                case "3":
                    createAutoPurchaseOrders();
                    break;
                case "4":
                    approveAllPendingOrders();
                    break;
                case "5":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }

        }
    }

    public static void showRealTimeIngredientPrices() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT i.name AS ingredient, s.name AS supplier, si.price_per_unit, si.updated_at
                FROM supplier_ingredients si
                JOIN ingredients i ON si.ingredient_id = i.ingredient_id
                JOIN suppliers s ON si.supplier_id = s.supplier_id
            """)) {
            System.out.println("\n📦 Real-Time Ingredient Prices:");
            while (rs.next()) {
                System.out.printf("Ingredient: %s | Supplier: %s | Price: %.2f | Updated: %s\n",
                        rs.getString("ingredient"),
                        rs.getString("supplier"),
                        rs.getDouble("price_per_unit"),
                        rs.getTimestamp("updated_at"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching supplier prices.");
            e.printStackTrace();
        }
    }

    public static void updateSupplierPrice(int ingredientId, double newPrice) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update = conn.prepareStatement(
                     "UPDATE supplier_ingredients SET price_per_unit = ?, updated_at = CURRENT_TIMESTAMP WHERE ingredient_id = ?");
             PreparedStatement updateOrders = conn.prepareStatement(
                     "UPDATE purchase_orders SET price = ? WHERE ingredient_id = ? AND status = 'Pending'")) {
            // 1. Update the supplier_ingredients table with new price and current timestamp
            update.setDouble(1, newPrice);
            update.setInt(2, ingredientId);
            update.executeUpdate();

            // 2. Update all pending purchase orders with the new price
            updateOrders.setDouble(1, newPrice);
            updateOrders.setInt(2, ingredientId);
            updateOrders.executeUpdate();

            System.out.println("✅ Updated supplier price and all pending purchase orders.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to update supplier price");
        }
    }
}
