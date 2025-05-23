package com.myproject.cooking1.entities;

import com.myproject.cooking1.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseHelper {

    private static boolean failMode = false;

    public static void simulateDatabaseFailure(boolean fail) {
        failMode = fail;
    }

    public static Connection getConnection() throws SQLException {
        if (failMode) {
            throw new SQLException("Simulated DB failure!");
        }
        return DBConnection.getConnection();
    }
    private static final String USER_ID = "user_id";

    public static int addChef(String name, String expertise, int activeTasks) {
        try (Connection conn = getConnection()) {
            String email = name.toLowerCase().replace(" ", "") + UUID.randomUUID().toString().substring(0, 6) + "@test.com";
            PreparedStatement insertChef = conn.prepareStatement(
                    "INSERT INTO Users (name, email, password, role, expertise) VALUES (?, ?, ?, 'chef', ?) RETURNING " + USER_ID
            );
            insertChef.setString(1, name);
            insertChef.setString(2, email);
            insertChef.setString(3, "password123");
            insertChef.setString(4, expertise);

            ResultSet rs = insertChef.executeQuery();
            if (rs.next()) {
                int chefId = rs.getInt(USER_ID);

                for (int i = 0; i < activeTasks; i++) {
                    PreparedStatement taskStmt = conn.prepareStatement(
                            "INSERT INTO Tasks (assigned_to, task_type, status) VALUES (?, 'cooking', 'active')"
                    );
                    taskStmt.setInt(1, chefId);
                    taskStmt.executeUpdate();
                }

                return chefId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getChefIdByName(String chefName) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM Users WHERE name = ? AND role = 'chef'");
            stmt.setString(1, chefName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    public static void setChefTaskCount(int chefId, int count) {
        try (Connection conn = getConnection()) {
            PreparedStatement delete = conn.prepareStatement("DELETE FROM Tasks WHERE assigned_to = ?");
            delete.setInt(1, chefId);
            delete.executeUpdate();

            for (int i = 0; i < count; i++) {
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO Tasks (assigned_to, task_type, status) VALUES (?, 'cooking', 'active')"
                );
                insert.setInt(1, chefId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearChefsAndTasks() {
        try (Connection conn = getConnection()) {
            conn.prepareStatement("DELETE FROM Tasks").executeUpdate();
            conn.prepareStatement("DELETE FROM Users WHERE role = 'chef'").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetChefsAndTasks() {
        clearChefsAndTasks();
    }

    public static void createPendingCookingTasks() {
        try (Connection conn = getConnection()) {
            for (int i = 0; i < 3; i++) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Tasks (task_type, status) VALUES ('cooking', 'pending')"
                );
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void createTaskAssignedToChefAndCustomer() {
        try (Connection conn = getConnection()) {
            int chefId = addChef("Chef Scenario", "AnyCuisine", 0);

            PreparedStatement insertCustomer = conn.prepareStatement(
                    "INSERT INTO Users (name, email, password, role) VALUES ('Customer Scenario', ?, 'pass', 'customer') RETURNING user_id"
            );
            insertCustomer.setString(1, "customer" + UUID.randomUUID().toString().substring(0,6) + "@test.com");
            ResultSet rs = insertCustomer.executeQuery();
            int customerId = -1;
            if (rs.next()) customerId = rs.getInt("user_id");

            PreparedStatement insertOrder = conn.prepareStatement(
                    "INSERT INTO Customized_Orders (customer_id) VALUES (?) RETURNING custom_order_id"
            );
            insertOrder.setInt(1, customerId);
            ResultSet orderRs = insertOrder.executeQuery();
            int customOrderId = -1;
            if (orderRs.next()) customOrderId = orderRs.getInt("custom_order_id");

            PreparedStatement insertTask = conn.prepareStatement(
                    "INSERT INTO Tasks (assigned_to, task_type, status, custom_order_id) VALUES (?, 'cooking', 'active', ?) RETURNING task_id"
            );
            insertTask.setInt(1, chefId);
            insertTask.setInt(2, customOrderId);
            ResultSet taskRs = insertTask.executeQuery();
            if (taskRs.next()) {
                int taskId = taskRs.getInt("task_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getLatestTaskId() {
        try (Connection conn = getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT task_id FROM Tasks ORDER BY task_id DESC LIMIT 1").executeQuery();
            if (rs.next()) return rs.getInt("task_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getCustomerIdForLatestTask() {
        try (Connection conn = getConnection()) {
            ResultSet rs = conn.prepareStatement(
                    "SELECT co.customer_id FROM Tasks t " +
                            "JOIN Customized_Orders co ON t.custom_order_id = co.custom_order_id " +
                            "ORDER BY t.task_id DESC LIMIT 1"
            ).executeQuery();
            if (rs.next()) return rs.getInt("customer_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }



    public static void createSpecificTaskAndChef() {
        clearChefsAndTasks();
        int chefId = addChef("Specific Chef", "TestCuisine", 0);
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Tasks (task_type, status) VALUES ('cooking', 'pending') RETURNING task_id"
            );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int taskId = rs.getInt("task_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getSpecificChefId() {
        return getChefIdByName("Specific Chef");
    }

    public static int getSpecificTaskId() {
        try (Connection conn = getConnection()) {
            ResultSet rs = conn.prepareStatement(
                    "SELECT task_id FROM Tasks WHERE status='pending' ORDER BY task_id DESC LIMIT 1"
            ).executeQuery();
            if (rs.next()) return rs.getInt("task_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int createUnlinkedTask(int chefId) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Tasks (assigned_to, task_type, status) VALUES (?, 'cooking', 'active') RETURNING task_id"
            );
            ps.setInt(1, chefId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("task_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static int addPendingTask() {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Tasks (task_type, status) VALUES ('cooking', 'pending') RETURNING task_id"
            );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("task_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }




}
