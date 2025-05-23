package com.myproject.cooking1.entities;

import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import com.myproject.cooking1.DBConnection;

public class User {
    private final int userId;
    private String name;
    private String email;
    private String password;
    private String role;
    private String expertise;

    public User(int userId, String name, String email, String password, String role, String expertise) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.expertise = expertise;
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
        String sql = "INSERT INTO Users (name, email, password, role, expertise) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getExpertise());
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
                            rs.getString("role"),
                            rs.getString("expertise")
                    );
                }
            }
        }
        return null;
    }

    public static User getUserById(int userId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("expertise")
                    );
                }
            }
        }
        return null;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public static List<Integer> getUserIdsByRole(String role) {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM Users WHERE role = ?")) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}
