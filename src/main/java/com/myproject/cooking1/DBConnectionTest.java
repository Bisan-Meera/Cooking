package com.myproject.cooking1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionTest {
    private static final String URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String USER = "postgres.qqdgnbhrovoinqjzrsna";
    private static final String PASSWORD = "bisan2005"; // use your real password

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("✅ Connected to Supabase (Session Pooler)!");
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect: " + e.getMessage());
        }
    }
}
