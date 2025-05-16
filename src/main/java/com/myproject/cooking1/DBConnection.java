package com.myproject.cooking1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = System.getenv("SUPABASE_DB_URL");
    private static final String USER = System.getenv("SUPABASE_DB_USER");
    private static final String PASSWORD = System.getenv("SUPABASE_DB_PASS");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
