package com.myproject.cooking1.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.myproject.cooking1.DBConnection;
public class NotificationService {



    public  void createNotification(int userId, String content) {
        try (Connection conn = DBConnection.getConnection()) {
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
