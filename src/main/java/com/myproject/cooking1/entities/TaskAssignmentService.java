package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskAssignmentService {

    public static int assignToLeastLoadedChef() {
        try (Connection conn = DatabaseHelper.getConnection()) {
            int chefId = findLeastLoadedChef();
            if (chefId == -1) return -1;
            return createTaskForChef(conn, chefId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int assignToChefWithExpertise(String cuisine) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.user_id FROM Users u " +
                             "LEFT JOIN Tasks t ON u.user_id = t.assigned_to AND t.status = 'active' " +
                             "WHERE u.role = 'chef' AND LOWER(u.expertise) = LOWER(?) " +
                             "GROUP BY u.user_id " +
                             "ORDER BY COUNT(t.task_id) ASC LIMIT 1")) {
            stmt.setString(1, cuisine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int chefId = rs.getInt("user_id");
                    System.out.println("✅ Selected Chef ID with expertise '" + cuisine + "': " + chefId);
                    return createTaskForChef(conn, chefId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int createTaskForChef(Connection conn, int chefId) throws SQLException {
        try (PreparedStatement assign = conn.prepareStatement(
                "INSERT INTO Tasks (assigned_to, task_type, status) VALUES (?, 'cooking', 'active') RETURNING task_id")) {
            assign.setInt(1, chefId);
            try (ResultSet result = assign.executeQuery()) {
                if (result.next()) {
                    int taskId = result.getInt("task_id");
                    NotificationService.createNotification(chefId, "New task assigned: #" + taskId);
                    return taskId;
                }
            }
        }
        return -1;
    }

    public static void showPendingTasksWithDetails() {
        System.out.print(capturePendingTasksWithDetails());
    }

    public static void showActiveTasksForChef(int chefId) {
        System.out.print(captureActiveTasksForChef(chefId));
    }

    // --- Helper for test output ---

    public static String capturePendingTasksWithDetails() {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT task_id, order_id, custom_order_id FROM Tasks WHERE assigned_to IS NULL OR assigned_to = 0");
             ResultSet rs = stmt.executeQuery()) {

            sb.append("Pending Tasks with Details:\n");
            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                int orderId = rs.getInt("order_id");
                int customOrderId = rs.getInt("custom_order_id");
                sb.append(" - Task ID: ").append(taskId).append("\n");

                if (customOrderId != 0) {
                    sb.append("   → Linked Custom Order ID: ").append(customOrderId).append("\n");
                    sb.append(showCustomOrderMealsString(conn, customOrderId));
                } else if (orderId != 0) {
                    sb.append("   → Linked Regular Order ID: ").append(orderId).append("\n");
                    sb.append(showRegularOrderMealsString(conn, orderId));
                } else {
                    sb.append("   → No linked order information.\n");
                }
                sb.append("\n");
            }
        } catch (SQLException e) {
            sb.append("❌ Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public static String captureActiveTasksForChef(int chefId) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT t.task_id, " +
                             "CASE " +
                             "   WHEN co.notes IS NOT NULL THEN 'Custom: ' || co.notes " +
                             "   WHEN m.name IS NOT NULL THEN 'Meal: ' || m.name " +
                             "   ELSE 'General cooking task' " +
                             "END AS description " +
                             "FROM Tasks t " +
                             "LEFT JOIN Customized_Orders co ON t.custom_order_id = co.custom_order_id " +
                             "LEFT JOIN Orders o ON t.order_id = o.order_id " +
                             "LEFT JOIN Order_Items oi ON o.order_id = oi.order_id " +
                             "LEFT JOIN Meals m ON oi.meal_id = m.meal_id " +
                             "WHERE t.assigned_to = ? " +
                             "  AND t.status = 'active' " +
                             "  AND t.task_type = 'cooking' " +
                             "GROUP BY t.task_id, co.notes, m.name " +
                             "ORDER BY t.task_id")) {
            ps.setInt(1, chefId);
            try (ResultSet rs = ps.executeQuery()) {
                sb.append("\n🔧 Your Active Tasks:\n");
                boolean found = false;
                while (rs.next()) {
                    int taskId = rs.getInt("task_id");
                    String desc = rs.getString("description");
                    sb.append(" - Task ID: ").append(taskId).append(" | ").append(desc).append("\n");
                    found = true;
                }
                if (!found) {
                    sb.append("✅ No active cooking tasks assigned to you.\n");
                }
            }
        } catch (SQLException e) {
            sb.append("❌ Error loading tasks.\n");
        }
        return sb.toString();
    }

    private static String showRegularOrderMealsString(Connection conn, int orderId) throws SQLException {
        StringBuilder sb = new StringBuilder();
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT m.name, oi.quantity FROM Order_Items oi JOIN Meals m ON oi.meal_id = m.meal_id WHERE oi.order_id = ?")) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sb.append("     • Meal: ").append(rs.getString("name"))
                            .append(" × ").append(rs.getInt("quantity")).append("\n");
                }
            }
        }
        return sb.toString();
    }

    private static String showCustomOrderMealsString(Connection conn, int customOrderId) throws SQLException {
        StringBuilder sb = new StringBuilder();
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT i.name, coi.quantity, coi.substitution FROM Customized_Order_Ingredients coi " +
                        "JOIN Ingredients i ON coi.ingredient_id = i.ingredient_id WHERE coi.custom_order_id = ?")) {
            stmt.setInt(1, customOrderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String substitution = rs.getString("substitution");
                    String note = (substitution != null && !substitution.isEmpty()) ? " (sub: " + substitution + ")" : "";
                    sb.append("     • Ingredient: ").append(rs.getString("name"))
                            .append(" × ").append(rs.getDouble("quantity")).append(note).append("\n");
                }
            }
        }
        return sb.toString();
    }

    // --- Main logic methods ---

    public static int getAssignedChef(int taskId) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT assigned_to FROM Tasks WHERE task_id = ?")) {
            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("assigned_to");
                }
            }
        }
        return -1;
    }

    public static int findLeastLoadedChef() throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.user_id, COUNT(t.task_id) as task_count " +
                             "FROM Users u LEFT JOIN Tasks t ON u.user_id = t.assigned_to AND t.status = 'active' " +
                             "WHERE u.role = 'chef' " +
                             "GROUP BY u.user_id " +
                             "ORDER BY task_count ASC LIMIT 1");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        return -1;
    }

    public static boolean hasNotification(int chefId, int taskId) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Notifications WHERE user_id = ? AND content LIKE ?")) {
            stmt.setInt(1, chefId);
            stmt.setString(2, "%" + taskId + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getChefExpertise(int chefId) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT expertise FROM Users WHERE user_id = ? AND role = 'chef'")) {
            stmt.setInt(1, chefId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("expertise");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> getAllChefsWithWorkloadAndExpertise() {
        List<User> chefs = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.user_id, u.name, u.email, u.password, u.role, u.expertise, COUNT(t.task_id) AS workload " +
                             "FROM Users u LEFT JOIN Tasks t ON u.user_id = t.assigned_to AND t.status = 'active' " +
                             "WHERE u.role = 'chef' GROUP BY u.user_id");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User chef = new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("expertise")
                );
                chefs.add(chef);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chefs;
    }

    public static String getTaskCount(int userId) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM Tasks WHERE assigned_to = ? AND status = 'active'")) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return String.valueOf(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static boolean assignTaskToChef(int taskId, int chefId) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Tasks SET assigned_to = ?, status = 'active' WHERE task_id = ?")) {
            stmt.setInt(1, chefId);
            stmt.setInt(2, taskId);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                NotificationService.createNotification(chefId, "You have been assigned task ID " + taskId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean markTaskAsReady(int taskId) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            // 1. Update task status
            try (PreparedStatement update = conn.prepareStatement(
                    "UPDATE Tasks SET status = 'ready' WHERE task_id = ?")) {
                update.setInt(1, taskId);
                int updated = update.executeUpdate();
                if (updated == 0) return false;
            }

            // 2. Fetch customer ID associated with this task (custom orders)
            try (PreparedStatement query = conn.prepareStatement(
                    "SELECT co.customer_id FROM Tasks t " +
                            "JOIN Customized_Orders co ON t.custom_order_id = co.custom_order_id " +
                            "WHERE t.task_id = ?")) {
                query.setInt(1, taskId);
                try (ResultSet rs = query.executeQuery()) {
                    if (rs.next()) {
                        int customerId = rs.getInt("customer_id");
                        String message = NotificationService.formatNotification("reminder", "Your meal is ready and will be delivered soon.");
                        NotificationService.createNotification(customerId, message);
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
