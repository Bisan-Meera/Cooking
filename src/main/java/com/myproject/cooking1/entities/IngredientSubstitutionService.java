package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;

import java.sql.*;
import java.util.*;

public class IngredientSubstitutionService {

    private static final Set<String> SEAFOOD_INGREDIENTS = Set.of(
            "salmon", "salmon fillet", "tilapia", "shrimp", "tuna"
    );

    public static List<String> suggestSubstitutionOptions(int customerId, String originalIngredient) throws SQLException {
        List<String> validCandidates = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("📥 Original ingredient: " + originalIngredient);

            // Short-circuit: if the original ingredient is available
            PreparedStatement stockStmt = conn.prepareStatement(
                    "SELECT stock_quantity FROM Ingredients WHERE name ILIKE ?"
            );
            stockStmt.setString(1, originalIngredient);
            ResultSet stockRs = stockStmt.executeQuery();
            if (stockRs.next() && stockRs.getDouble("stock_quantity") >= 0.1) {
                System.out.println("✅ Ingredient in stock. No substitution needed.");
                return validCandidates; // empty list = no substitution needed
            }

            // Fetch dietary and allergy info
            PreparedStatement prefStmt = conn.prepareStatement(
                    "SELECT dietary_preference, allergy FROM CustomerPreferences WHERE user_id = ?"
            );
            prefStmt.setInt(1, customerId);
            ResultSet prefsRs = prefStmt.executeQuery();
            String dietary = "", allergy = "";
            if (prefsRs.next()) {
                dietary = prefsRs.getString("dietary_preference").toLowerCase();
                allergy = prefsRs.getString("allergy").toLowerCase();
            }

            System.out.println("👤 Customer #" + customerId + ", dietary: " + dietary + ", allergy: " + allergy);

            // Fetch substitution candidates
            PreparedStatement ingStmt = conn.prepareStatement(
                    "SELECT name FROM Ingredients WHERE name != ? AND stock_quantity >= 0.1"
            );
            ingStmt.setString(1, originalIngredient);
            ResultSet rs = ingStmt.executeQuery();

            while (rs.next()) {
                String candidate = rs.getString("name");
                String candidateLower = candidate.toLowerCase();

                System.out.println("🔍 Candidate: " + candidate);

                // Filter by dietary restrictions
                if (dietary.contains("vegetarian") && isMeat(candidateLower)) {
                    System.out.println("❌ Skipped (meat for vegetarian): " + candidate);
                    continue;
                }

                if (dietary.contains("vegan") && (isMeat(candidateLower) || isDairy(candidateLower))) {
                    System.out.println("❌ Skipped (animal product for vegan): " + candidate);
                    continue;
                }

                // Filter by seafood allergy
                if (allergy.contains("seafood") && SEAFOOD_INGREDIENTS.contains(candidateLower)) {
                    System.out.println("❌ Skipped (allergy to seafood): " + candidate);
                    continue;
                }

                // General allergy string match
                if (!allergy.isBlank() && candidateLower.contains(allergy)) {
                    System.out.println("❌ Skipped (matches allergy): " + candidate);
                    continue;
                }

                validCandidates.add(capitalize(candidate));
            }

            if (!validCandidates.isEmpty()) {
                System.out.println("✅ Valid substitutions: " + validCandidates);
            } else {
                System.out.println("❌ No valid substitutions found.");
            }
        }

        return validCandidates;
    }

    public static void recordSubstitution(int customOrderId, String original, String substitute) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement getIng = conn.prepareStatement(
                    "SELECT ingredient_id FROM Ingredients WHERE name ILIKE ?"
            );
            getIng.setString(1, substitute);
            ResultSet rs = getIng.executeQuery();
            if (!rs.next()) throw new SQLException("Substitution ingredient not found");

            int ingId = rs.getInt("ingredient_id");

            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Customized_Order_Ingredients (custom_order_id, ingredient_id, quantity, substitution) VALUES (?, ?, ?, ?)"
            );
            insert.setInt(1, customOrderId);
            insert.setInt(2, ingId);
            insert.setDouble(3, 0.10); // fixed usage
            insert.setString(4, original);
            insert.executeUpdate();
        }
        NotificationService.notifyChefsOfSubstitution(original, substitute);
    }

    public static void createSubstitutionTask(int customOrderId, String original, String substitute, int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement orderStmt = conn.prepareStatement(
                    "INSERT INTO Orders (customer_id, status) VALUES (?, ?) RETURNING order_id"
            );
            orderStmt.setInt(1, customerId);
            orderStmt.setString(2, "pending");
            ResultSet orderRs = orderStmt.executeQuery();

            if (!orderRs.next()) throw new SQLException("Failed to create linked order");
            int newOrderId = orderRs.getInt("order_id");

            PreparedStatement taskStmt = conn.prepareStatement(
                    "INSERT INTO Tasks (order_id, task_type, status) VALUES (?, ?, ?)"
            );
            taskStmt.setInt(1, newOrderId);
            taskStmt.setString(2, "ingredient_substitution");
            taskStmt.setString(3, "pending");
            taskStmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isMeat(String ingredientName) {
        return ingredientName.matches("(?i).*\\b(beef|chicken|lamb|salmon|tilapia)\\b.*");
    }

    private static boolean isDairy(String ingredientName) {
        return ingredientName.matches("(?i).*\\b(cheese|milk|butter|yogurt|cream|mozzarella)\\b.*");
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
