package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerProfileService {

    public void updatePreferences(int userId, String selectedPreference, String selectedAllergy) {
        String sql = "INSERT INTO CustomerPreferences (user_id, dietary_preference, allergy) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (user_id) DO UPDATE SET dietary_preference = EXCLUDED.dietary_preference, allergy = EXCLUDED.allergy";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, selectedPreference);
            stmt.setString(3, selectedAllergy);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while updating preferences");
        }
    }

    public CustomerPreferences viewPreferences(int userId) {
        String sql = "SELECT dietary_preference, allergy FROM CustomerPreferences WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new CustomerPreferences(rs.getString("dietary_preference"), rs.getString("allergy"));
            } else {
                return new CustomerPreferences("", "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching preferences");
        }
    }
}
