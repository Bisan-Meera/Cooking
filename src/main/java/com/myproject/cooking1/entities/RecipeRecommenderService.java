package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;
import java.sql.*;
import java.util.*;

public class RecipeRecommenderService {

    public static List<Map<String, Object>> recommendMeals(String dietary, String allergy, int maxTime, List<String> availableIngredients) {
        List<Map<String, Object>> recommended = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String mealQuery = "SELECT m.meal_id, m.name, m.description, m.preparation_time " +
                    "FROM meals m WHERE m.preparation_time <= ?";
            PreparedStatement mealStmt = conn.prepareStatement(mealQuery);
            mealStmt.setInt(1, maxTime);
            ResultSet meals = mealStmt.executeQuery();

            while (meals.next()) {
                int mealId = meals.getInt("meal_id");
                String name = meals.getString("name");
                String description = meals.getString("description");
                int time = meals.getInt("preparation_time");

                if (!isMealAllowed(mealId, dietary, allergy, availableIngredients, conn)) continue;

                Map<String, Object> meal = new HashMap<>();
                meal.put("name", name);
                meal.put("description", description);
                meal.put("preparation_time", time);
                meal.put("ingredients", getMealIngredients(mealId, conn));

                recommended.add(meal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recommended;
    }

    private static boolean isMealAllowed(int mealId, String dietary, String allergy, List<String> available, Connection conn) throws SQLException {
        String query = "SELECT i.name FROM meal_ingredients mi JOIN ingredients i ON mi.ingredient_id = i.ingredient_id WHERE mi.meal_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, mealId);
        ResultSet rs = stmt.executeQuery();

        List<String> requiredIngredients = new ArrayList<>();
        while (rs.next()) {
            String ing = rs.getString("name").toLowerCase();
            if (!available.contains(ing)) return false;

            if (allergy != null && !allergy.isBlank() && ing.contains(allergy.toLowerCase())) return false;
            if (dietary != null && dietary.equalsIgnoreCase("Vegan") && isNonVegan(ing)) return false;
            if (dietary != null && dietary.equalsIgnoreCase("Vegetarian") && isMeat(ing)) return false;
            if (dietary != null && dietary.equalsIgnoreCase("Halal") && isNonHalal(ing)) return false;

            requiredIngredients.add(ing);
        }
        return !requiredIngredients.isEmpty();
    }

    private static List<String> getMealIngredients(int mealId, Connection conn) throws SQLException {
        List<String> ingredients = new ArrayList<>();
        String query = "SELECT i.name FROM meal_ingredients mi JOIN ingredients i ON mi.ingredient_id = i.ingredient_id WHERE mi.meal_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, mealId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ingredients.add(rs.getString("name"));
        }
        return ingredients;
    }

    private static boolean isMeat(String name) {
        return name.matches("(?i).*\\b(chicken|beef|lamb|meat|salmon|tilapia)\\b.*");
    }

    private static boolean isNonVegan(String name) {
        return name.matches("(?i).*\\b(cheese|milk|butter|cream|yogurt|eggs|meat|fish)\\b.*");
    }

    private static boolean isNonHalal(String name) {
        return name.matches("(?i).*\\b(pork|ham|bacon|alcohol)\\b.*");
    }
}
