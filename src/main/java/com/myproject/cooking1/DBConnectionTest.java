package com.myproject.cooking1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionTest {
    private static final String URL = "jdbc:postgresql://db.qqdgnbhrovoinqjzrsna.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "YOUR_SUPABASE_PASSWORD"; // replace this

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("✅ Connected to Supabase!");
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect: " + e.getMessage());
        }
    }
}

