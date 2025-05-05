package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomOrderService {
    public static boolean submitCustomMeal(int userId, List<String> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("At least one ingredient must be selected");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Check stock availability for all ingredients
            for (String name : ingredients) {
                PreparedStatement checkStock = conn.prepareStatement(
                        "SELECT stock_quantity FROM Ingredients WHERE name ILIKE ?"
                );
                checkStock.setString(1, name);
                ResultSet rs = checkStock.executeQuery();

                if (rs.next()) {
                    double qty = rs.getDouble("stock_quantity");
                    if (qty < 0.10) {
                        throw new IllegalArgumentException(capitalize(name) + " is unavailable");
                    }
                } else {
                    throw new IllegalArgumentException("Ingredient not found: " + name);
                }
            }

            // Create custom order (basic version, linking only to user)
            PreparedStatement insertOrder = conn.prepareStatement(
                    "INSERT INTO Customized_Orders (customer_id, notes) VALUES (?, ?) RETURNING custom_order_id"
            );
            insertOrder.setInt(1, userId);
            insertOrder.setString(2, "Custom meal with ingredients: " + String.join(", ", ingredients));
            ResultSet orderRs = insertOrder.executeQuery();

            if (!orderRs.next()) throw new SQLException("Failed to create custom order");

            int customOrderId = orderRs.getInt("custom_order_id");

            //  Insert corresponding unassigned cooking task
            PreparedStatement insertTask = conn.prepareStatement(
                    "INSERT INTO Tasks (custom_order_id, task_type, status) VALUES (?, 'cooking', 'active')"
            );
            insertTask.setInt(1, customOrderId);
            insertTask.executeUpdate();


            // Deduct stock and save ingredients
            for (String name : ingredients) {
                PreparedStatement getId = conn.prepareStatement("SELECT ingredient_id FROM Ingredients WHERE name ILIKE ?");
                getId.setString(1, name);
                ResultSet idRs = getId.executeQuery();

                if (!idRs.next()) throw new SQLException("Ingredient ID not found for: " + name);
                int ingId = idRs.getInt("ingredient_id");
               // double currentQty = idRs.getDouble("stock_quantity") - 0.10;
                //double threshold = idRs.getDouble("threshold");
               // String ingName = idRs.getString("name");
                // Save ingredient usage
                PreparedStatement insertUsage = conn.prepareStatement(
                        "INSERT INTO Customized_Order_Ingredients (custom_order_id, ingredient_id, quantity) VALUES (?, ?, ?)"
                );
                insertUsage.setInt(1, customOrderId);
                insertUsage.setInt(2, ingId);
                insertUsage.setDouble(3, 0.10);
                insertUsage.executeUpdate();

                // Deduct stock
                PreparedStatement deduct = conn.prepareStatement(
                        "UPDATE Ingredients SET stock_quantity = stock_quantity - 0.10 WHERE ingredient_id = ?"
                );
                deduct.setInt(1, ingId);
                deduct.executeUpdate();

            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save custom meal due to system error");
        }
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }



}