package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    public static void createNotification(int userId, String content) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO notifications (user_id, content, is_read, created_at) VALUES (?, ?, false, CURRENT_TIMESTAMP)"
             )) {
            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void markNotificationsAsRead(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE notifications SET is_read = true WHERE user_id = ? AND is_read = false"
             )) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void notifyAllByRole(String role, String message) {
        List<Integer> users = User.getUserIdsByRole(role);
        for (int id : users) {
            createNotification(id, message);
        }
    }

    public static List<String> getUnreadNotifications(int userId) {
        List<String> notifications = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT content FROM notifications WHERE user_id = ? AND is_read = false"
             )) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(rs.getString("content"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public static String formatNotification(String type, String detail) {
        return switch (type) {
            case "reminder" -> "Reminder: " + detail;
            case "restock" -> "Stock Alert: " + detail;
            case "task" -> "Task Reminder: " + detail;
            case "substitution" -> "Substitution: " + detail;
            default -> "Notice: " + detail;
        };
    }

    public static void notifyChefsOfSubstitution(String original, String substitute) {
        String message = formatNotification("substitution", "Substitution applied: " + original + " → " + substitute);
        notifyAllByRole("chef", message);
    }

    // Returns true if any unread notification for this chef contains the given task ID
    public static boolean chefHasNotificationWithTaskId(int chefId, int taskId) {
        for (String content : getUnreadNotifications(chefId)) {
            if (content != null && content.contains(String.valueOf(taskId))) {
                return true;
            }
        }
        return false;
    }

    // Returns true if any unread notification for this customer contains the given string
    public static boolean customerHasNotificationContent(int customerId, String content) {
        for (String n : getUnreadNotifications(customerId)) {
            if (n != null && n.contains(content)) {
                return true;
            }
        }
        return false;
    }
}
