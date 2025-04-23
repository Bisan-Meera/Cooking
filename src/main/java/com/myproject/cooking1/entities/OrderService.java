package com.myproject.cooking1.entities;



import com.myproject.cooking1.DBConnection;

import java.sql.*;
import java.util.*;

public class OrderService {
    private final Connection conn;

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

    public Map<Integer, List<String>> getAllCustomerOrders() {
        Map<Integer, List<String>> allOrders = new HashMap<>();
        String query = "SELECT o.customer_id, m.name " +
                "FROM Orders o " +
                "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                "JOIN Meals m ON oi.meal_id = m.meal_id";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String meal = rs.getString("name");

                allOrders.computeIfAbsent(customerId, k -> new ArrayList<>()).add(meal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allOrders;
    }
}