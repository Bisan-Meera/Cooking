package com.myproject.cooking1;

import com.myproject.cooking1.entities.CustomerPreferences;
import com.myproject.cooking1.entities.CustomerProfileService;
import com.myproject.cooking1.entities.User;

import java.sql.Connection;
import java.util.Scanner;

public class SimpleLogin {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter user ID: ");
        int userId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();

        try (Connection conn = DBConnection.getConnection()) {
            User user = User.getUserByIdAndName(userId, name, conn);
            if (user != null) {
                System.out.println("User role: " + user.getRole());
            } else {
                System.out.println("Invalid user ID or name.");
            }
        } catch (Exception e) {
            System.out.println("Login failed due to system error.");
            e.printStackTrace();
        }


            CustomerProfileService service = new CustomerProfileService();
            CustomerPreferences prefs = service.viewPreferences(1); // for Layla Hassan

            System.out.println("Dietary: " + prefs.getDietaryPreference());
            System.out.println("Allergy: " + prefs.getAllergy());



    }
}
