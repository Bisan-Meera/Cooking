package com.myproject.cooking1.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationService {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/yourdb", "youruser", "yourpass");
    }

    public  void createNotification(int userId, String content) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO notifications (user_id, content, is_read, created_at) VALUES (?, ?, false, CURRENT_TIMESTAMP)"
            );
            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
