package com.myproject.cooking1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/Cooking1";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "bisan2005";

    private static final String URL = System.getenv("SUPABASE_DB_URL") != null ? System.getenv("SUPABASE_DB_URL") : DEFAULT_URL;
    private static final String USER = System.getenv("SUPABASE_DB_USER") != null ? System.getenv("SUPABASE_DB_USER") : DEFAULT_USER;
    private static final String PASSWORD = System.getenv("SUPABASE_DB_PASS") != null ? System.getenv("SUPABASE_DB_PASS") : DEFAULT_PASSWORD;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
