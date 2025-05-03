package com.myproject.cooking1.entities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientStockService {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/yourdb", "youruser", "yourpass");
    }

    public void deductIngredientsForOrder(int orderId) {
        try (Connection conn = getConnection()) {
            String query = "SELECT mi.ingredient_id, SUM(mi.quantity * oi.quantity) AS total_required " +
                    "FROM order_items oi " +
                    "JOIN meal_ingredients mi ON oi.meal_id = mi.meal_id " +
                    "WHERE oi.order_id = ? " +
                    "GROUP BY mi.ingredient_id";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, orderId);
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

    public List<String> getLowStockIngredients() {
        List<String> lowStock = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT name FROM ingredients WHERE stock_quantity <= threshold";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                lowStock.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStock;
    }

    public void updateIngredientStock(int ingredientId, double newQty) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ingredients SET stock_quantity = ?, last_updated = CURRENT_TIMESTAMP WHERE ingredient_id = ?");
            ps.setDouble(1, newQty);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isIngredientBelowThreshold(int ingredientId) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT stock_quantity, threshold FROM ingredients WHERE ingredient_id = ?");
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("stock_quantity") <= rs.getDouble("threshold");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getIngredientName(int ingredientId) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM ingredients WHERE ingredient_id = ?");
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
