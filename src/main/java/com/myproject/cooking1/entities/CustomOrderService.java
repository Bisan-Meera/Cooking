package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class CustomOrderService {

    // Constants
    private static final String USER_ID = "user_id";
    private static final String CUSTOM_ORDER_ID = "custom_order_id";
    private static final String INGREDIENT_ID = "ingredient_id";
    private static final double DEFAULT_QUANTITY = 0.10;

    public static boolean FORCE_DB_ERROR = false;

    public static boolean submitCustomMeal(int userId, List<String> ingredients, Map<String, String> substitutions) {
        validateIngredients(ingredients);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            checkAllIngredientsStock(conn, ingredients);

            if (FORCE_DB_ERROR) throw new SQLException("Simulated database failure");

            int customOrderId = createCustomOrder(conn, userId, ingredients);

            insertTask(conn, customOrderId);

            deductStockAndSaveUsage(conn, customOrderId, ingredients);

            handleSubstitutionsAndNotify(customOrderId, substitutions);

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save custom meal due to system error");
        }
    }

    // -------------- Helpers -----------------

    private static void validateIngredients(List<String> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("At least one ingredient must be selected");
        }
    }

    private static void checkAllIngredientsStock(Connection conn, List<String> ingredients) throws SQLException {
        for (String name : ingredients) {
            double qty = getStockQuantity(conn, name);
            if (qty < DEFAULT_QUANTITY) {
                String capitalized = capitalize(name);
                throw new IllegalArgumentException(capitalized + " is unavailable");
            }
        }
    }

    private static double getStockQuantity(Connection conn, String name) throws SQLException {
        PreparedStatement checkStock = conn.prepareStatement(
                "SELECT stock_quantity FROM Ingredients WHERE name ILIKE ?"
        );
        checkStock.setString(1, name);
        ResultSet rs = checkStock.executeQuery();
        if (rs.next()) {
            return rs.getDouble("stock_quantity");
        } else {
            throw new IllegalArgumentException("Ingredient not found: " + name);
        }
    }

    private static int createCustomOrder(Connection conn, int userId, List<String> ingredients) throws SQLException {
        PreparedStatement insertOrder = conn.prepareStatement(
                "INSERT INTO Customized_Orders (customer_id, notes) VALUES (?, ?) RETURNING " + CUSTOM_ORDER_ID
        );
        insertOrder.setInt(1, userId);
        insertOrder.setString(2, "Custom meal with ingredients: " + String.join(", ", ingredients));
        ResultSet orderRs = insertOrder.executeQuery();
        if (!orderRs.next()) throw new SQLException("Failed to create custom order");
        return orderRs.getInt(CUSTOM_ORDER_ID);
    }

    private static void insertTask(Connection conn, int customOrderId) throws SQLException {
        PreparedStatement insertTask = conn.prepareStatement(
                "INSERT INTO Tasks (custom_order_id, task_type, status) VALUES (?, 'cooking', 'active')"
        );
        insertTask.setInt(1, customOrderId);
        insertTask.executeUpdate();
    }

    private static void deductStockAndSaveUsage(Connection conn, int customOrderId, List<String> ingredients) throws SQLException {
        for (String name : ingredients) {
            int ingId = getIngredientId(conn, name);
            insertUsage(conn, customOrderId, ingId);
            deductStock(conn, ingId);
        }
    }

    private static int getIngredientId(Connection conn, String name) throws SQLException {
        PreparedStatement getId = conn.prepareStatement(
                "SELECT ingredient_id FROM Ingredients WHERE name ILIKE ?"
        );
        getId.setString(1, name);
        ResultSet idRs = getId.executeQuery();
        if (!idRs.next()) throw new SQLException("Ingredient ID not found for: " + name);
        return idRs.getInt(INGREDIENT_ID);
    }

    private static void insertUsage(Connection conn, int customOrderId, int ingId) throws SQLException {
        PreparedStatement insertUsage = conn.prepareStatement(
                "INSERT INTO Customized_Order_Ingredients (custom_order_id, ingredient_id, quantity) VALUES (?, ?, ?)"
        );
        insertUsage.setInt(1, customOrderId);
        insertUsage.setInt(2, ingId);
        insertUsage.setDouble(3, DEFAULT_QUANTITY);
        insertUsage.executeUpdate();
    }

    private static void deductStock(Connection conn, int ingId) throws SQLException {
        PreparedStatement deduct = conn.prepareStatement(
                "UPDATE Ingredients SET stock_quantity = stock_quantity - ? WHERE ingredient_id = ?"
        );
        deduct.setDouble(1, DEFAULT_QUANTITY);
        deduct.setInt(2, ingId);
        deduct.executeUpdate();
    }

    private static void handleSubstitutionsAndNotify(int customOrderId, Map<String, String> substitutions) throws SQLException {
        for (Map.Entry<String, String> entry : substitutions.entrySet()) {
            IngredientSubstitutionService.recordSubstitution(customOrderId, entry.getKey(), entry.getValue());
        }

        if (!substitutions.isEmpty()) {
            String content = NotificationService.formatNotification(
                    "task", "Ingredient substitutions have been applied to custom order #" + customOrderId
            );
            NotificationService.notifyAllByRole("chef", content);
        }
    }

    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
