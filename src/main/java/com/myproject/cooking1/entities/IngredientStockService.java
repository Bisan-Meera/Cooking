package com.myproject.cooking1.entities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.myproject.cooking1.DBConnection;

public class IngredientStockService {

    public void deductIngredientsForOrder(int orderId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT mi.ingredient_id, SUM(mi.quantity * oi.quantity) AS total_required " +
                    "FROM order_items oi " +
                    "JOIN meal_ingredients mi ON oi.meal_id = mi.meal_id " +
                    "WHERE oi.order_id = ? " +
                    "GROUP BY mi.ingredient_id";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int ingredientId = rs.getInt("ingredient_id");
                        double required = rs.getDouble("total_required");

                        if (ingredientId == 17) {
                            System.out.println("🍽 [DEBUG] Onions deduction required=" + required);
                        }

                        System.out.println("Deducting ingredient_id: " + ingredientId + " amount: " + required);
                        System.out.println("Updating ingredient ID: " + ingredientId + ", will deduct: " + required);

                        try (PreparedStatement update = conn.prepareStatement(
                                "UPDATE ingredients SET stock_quantity = stock_quantity - ?, last_updated = CURRENT_TIMESTAMP WHERE ingredient_id = ?")) {
                            update.setDouble(1, required);
                            update.setInt(2, ingredientId);
                            int rowsAffected = update.executeUpdate();
                            System.out.println("Deduction executed for ingredient ID: " + ingredientId + " (rows affected: " + rowsAffected + ")");
                        }

                        // 🚨 Check stock threshold after deduction and notify if needed
                        try (PreparedStatement check = conn.prepareStatement(
                                "SELECT name, stock_quantity, threshold FROM ingredients WHERE ingredient_id = ?")) {
                            check.setInt(1, ingredientId);
                            try (ResultSet checkRs = check.executeQuery()) {
                                if (checkRs.next()) {
                                    double stock = checkRs.getDouble("stock_quantity");
                                    double threshold = checkRs.getDouble("threshold");
                                    String name = checkRs.getString("name");
                                    if (stock <= threshold) {
                                        String msg = NotificationService.formatNotification("restock", name + " is below threshold.");
                                        NotificationService.notifyAllByRole("kitchen_staff", msg);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLowStockIngredients() {
        List<String> lowStock = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM ingredients WHERE stock_quantity <= threshold")) {

            while (rs.next()) {
                lowStock.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStock;
    }

    public void updateIngredientStock(int ingredientId, double newQty) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE ingredients SET stock_quantity = ?, last_updated = CURRENT_TIMESTAMP WHERE ingredient_id = ?")) {
            ps.setDouble(1, newQty);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isIngredientBelowThreshold(int ingredientId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT stock_quantity, threshold FROM ingredients WHERE ingredient_id = ?")) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("stock_quantity") <= rs.getDouble("threshold");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getIngredientName(int ingredientId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM ingredients WHERE ingredient_id = ?")) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
