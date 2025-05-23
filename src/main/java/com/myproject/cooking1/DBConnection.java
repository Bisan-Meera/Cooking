package com.myproject.cooking1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = System.getenv("DB_URL") != null ?
            System.getenv("DB_URL") :
            "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String USER = "postgres.qqdgnbhrovoinqjzrsna";
    private static final String PASSWORD = System.getenv("DBPASS");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


