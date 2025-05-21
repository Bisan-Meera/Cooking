package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;

import java.sql.*;
import java.util.*;

public class OrderService {
    private final Connection conn;
    public static boolean FORCE_DB_ERROR = false;

    public OrderService() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    public List<Map<String, String>> getCustomerOrderHistory(int userId) {
        List<Map<String, String>> orders = new ArrayList<>();
        String query = "SELECT m.name, m.price, m.description " +
                "FROM Orders o " +
                "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                "JOIN Meals m ON oi.meal_id = m.meal_id " +
                "WHERE o.customer_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> meal = new HashMap<>();
                meal.put("name", rs.getString("name"));
                meal.put("price", rs.getString("price"));
                meal.put("description", rs.getString("description"));
                orders.add(meal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public int createOrder(int customerId, List<String> meals) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            if (FORCE_DB_ERROR) throw new SQLException("Simulated error for test");

            // ✅ STEP 1: Check ingredient stock for each meal
            for (String meal : meals) {
                PreparedStatement getMealIdStmt = conn.prepareStatement("SELECT meal_id FROM Meals WHERE name = ?");
                getMealIdStmt.setString(1, meal);
                ResultSet mealIdRs = getMealIdStmt.executeQuery();

                if (!mealIdRs.next()) throw new SQLException("Meal not found: " + meal);
                int mealId = mealIdRs.getInt("meal_id");

                PreparedStatement ingStmt = conn.prepareStatement(
                        "SELECT mi.ingredient_id, mi.quantity, i.stock_quantity, i.threshold " +
                                "FROM meal_ingredients mi JOIN ingredients i ON mi.ingredient_id = i.ingredient_id " +
                                "WHERE mi.meal_id = ?"
                );
                ingStmt.setInt(1, mealId);
                ResultSet ingRs = ingStmt.executeQuery();

                while (ingRs.next()) {
                    double requiredQty = ingRs.getDouble("quantity");
                    double stockQty = ingRs.getDouble("stock_quantity");
                    double threshold = ingRs.getDouble("threshold");

                    if (stockQty < requiredQty || stockQty <= threshold) {
                        throw new RuntimeException("Cannot order '" + meal + "' due to low stock");


                    }
                }
            }

            // ✅ STEP 2: Insert into Orders
            PreparedStatement orderStmt = conn.prepareStatement(
                    "INSERT INTO Orders (customer_id, status, created_at) VALUES (?, ?, CURRENT_TIMESTAMP) RETURNING order_id"
            );
            orderStmt.setInt(1, customerId);
            orderStmt.setString(2, "pending");
            ResultSet rs = orderStmt.executeQuery();
            rs.next();
            int orderId = rs.getInt("order_id");

            // ✅ STEP 3: Link meals to order
            for (String meal : meals) {
                PreparedStatement mealStmt = conn.prepareStatement(
                        "INSERT INTO Order_Items (order_id, meal_id, quantity) " +
                                "SELECT ?, meal_id, ? FROM Meals WHERE name = ?"
                );
                mealStmt.setInt(1, orderId);
                mealStmt.setInt(2, 1);
                mealStmt.setString(3, meal);
                mealStmt.executeUpdate();
            }

            // ✅ STEP 4: Deduct stock
            for (String meal : meals) {
                PreparedStatement getMealIdStmt = conn.prepareStatement("SELECT meal_id FROM Meals WHERE name = ?");
                getMealIdStmt.setString(1, meal);
                ResultSet mealIdRs = getMealIdStmt.executeQuery();
                mealIdRs.next();
                int mealId = mealIdRs.getInt("meal_id");

                PreparedStatement ingStmt = conn.prepareStatement(
                        "SELECT ingredient_id, quantity FROM meal_ingredients WHERE meal_id = ?"
                );
                ingStmt.setInt(1, mealId);
                ResultSet ingRs = ingStmt.executeQuery();

                while (ingRs.next()) {
                    int ingredientId = ingRs.getInt("ingredient_id");
                    double usedQty = ingRs.getDouble("quantity");

                    PreparedStatement updateStock = conn.prepareStatement(
                            "UPDATE ingredients SET stock_quantity = stock_quantity - ? WHERE ingredient_id = ?"
                    );
                    updateStock.setDouble(1, usedQty);
                    updateStock.setInt(2, ingredientId);
                    updateStock.executeUpdate();
                }
            }

            // ✅ STEP 5: Create task
            PreparedStatement taskStmt = conn.prepareStatement(
                    "INSERT INTO Tasks (order_id, task_type, status) VALUES (?, ?, ?)"
            );
            taskStmt.setInt(1, orderId);
            taskStmt.setString(2, "order-prep");
            taskStmt.setString(3, "pending");
            taskStmt.executeUpdate();

            conn.commit();
            return orderId;

        } catch (Exception e) {
        conn.rollback();
        if (e instanceof RuntimeException) throw (RuntimeException) e; // forward the original error
        throw new RuntimeException("Failed to create order", e);
    }
 finally {
            conn.close();
        }
    }


    public static List<String> getTasksByOrderId(int orderId) throws SQLException {
        List<String> tasks = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT status FROM Tasks WHERE order_id = ?"
        );
        stmt.setInt(1, orderId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            tasks.add(rs.getString("status"));
        }
        conn.close();
        return tasks;
    }


}
