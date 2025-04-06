package com.myproject.cooking1.entities;

import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

import com.myproject.cooking1.DBConnection;

public class User {
    private final int userId;
    private String name;
    private String email;
    private String password;
    private String role;

    public static final int UPDATE_NAME = 1;
    public static final int UPDATE_EMAIL = 2;
    public static final int UPDATE_PASSWORD = 3;
    public static final int UPDATE_ROLE = 4;
    public static final int DELETE_USER = 5;

    public User(int userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() {
        return Objects.equals(this.role, "admin");
    }

    public boolean isChef() {
        return Objects.equals(this.role, "chef");
    }

    public boolean isCustomer() {
        return Objects.equals(this.role, "customer");
    }

    public boolean isKitchenStaff() {
        return Objects.equals(this.role, "kitchen_staff");
    }

    public static void createUser(User user, Connection conn) throws SQLException {
        String sql = "INSERT INTO Users (user_id, name, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getUserId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());
            stmt.executeUpdate();
        }
    }

    public static User getUserByIdAndName(int userId, String name, Connection conn) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_id = ? AND name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    public static boolean updateUser(User user, Connection conn, int updateType) throws SQLException {
        String sql;
        switch (updateType) {
            case UPDATE_NAME:
                sql = "UPDATE Users SET name = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, user.getName());
                    stmt.setInt(2, user.getUserId());
                    return stmt.executeUpdate() > 0;
                }
            case UPDATE_EMAIL:
                sql = "UPDATE Users SET email = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, user.getEmail());
                    stmt.setInt(2, user.getUserId());
                    return stmt.executeUpdate() > 0;
                }
            case UPDATE_PASSWORD:
                sql = "UPDATE Users SET password = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, user.getPassword());
                    stmt.setInt(2, user.getUserId());
                    return stmt.executeUpdate() > 0;
                }
            case UPDATE_ROLE:
                sql = "UPDATE Users SET role = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, user.getRole());
                    stmt.setInt(2, user.getUserId());
                    return stmt.executeUpdate() > 0;
                }
            case DELETE_USER:
                return deleteUserById(user.getUserId(), conn);
            default:
                return false;
        }
    }

    public static boolean deleteUserById(int userId, Connection conn) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
