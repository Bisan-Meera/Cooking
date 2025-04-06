package com.myproject.cooking1;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionTest {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("✅ Connection successful!");
                conn.close();
            } else {
                System.out.println("❌ Connection returned null.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to database.");
            e.printStackTrace();
        }
    }
}
