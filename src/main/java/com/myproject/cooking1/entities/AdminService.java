package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;

import java.sql.*;
import java.util.*;

public class AdminService {

    private final Connection connection;

    public AdminService() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    public Map<Integer, List<String>> getAllCustomerOrders() {
        Map<Integer, List<String>> orders = new HashMap<>();

        String query = """
            SELECT o.customer_id, m.name
            FROM Orders o
            JOIN Order_Items oi ON o.order_id = oi.order_id
            JOIN Meals m ON oi.meal_id = m.meal_id
            ORDER BY o.customer_id
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String mealName = rs.getString("name");

                orders.computeIfAbsent(customerId, k -> new ArrayList<>()).add(mealName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
