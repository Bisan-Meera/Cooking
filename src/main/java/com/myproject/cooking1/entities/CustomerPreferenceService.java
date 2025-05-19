package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerPreferenceService {
    public static CustomerPreferences getPreferencesByCustomerId(int userId) throws SQLException {
        if (userId == -999) {
            throw new SQLException("Simulated DB failure");
        }
        String query = "SELECT dietary_preference, allergy FROM CustomerPreferences WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = ((Connection) conn).prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CustomerPreferences(
                            rs.getString("dietary_preference"),
                            rs.getString("allergy")
                    );
                } else {
                    return null;
                }
            }
        }
    }
}

