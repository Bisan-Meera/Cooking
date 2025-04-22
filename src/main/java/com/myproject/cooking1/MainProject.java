package com.myproject.cooking1;

import com.myproject.cooking1.entities.CustomerPreferences;
import com.myproject.cooking1.entities.CustomerProfileService;
import com.myproject.cooking1.entities.User;

import java.sql.Connection;
import java.util.Scanner;

public class MainProject {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        User user = null;

        // Keep prompting until valid login
        while (user == null) {
            System.out.print("Enter user ID: ");
            int userId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            try (Connection conn = DBConnection.getConnection()) {
                user = User.getUserByIdAndName(userId, name, conn);
                if (user == null) {
                    System.out.println("Invalid user ID or name. Please try again.\n");
                } else {
                    System.out.println("Login successful. Role: " + user.getRole());
                    // Route based on role
                    switch (user.getRole()) {
                        case "customer":
                            launchCustomerPage(user, scanner); // pass the whole user object
                            break;
                        case "chef":
                            System.out.println("Chef page coming soon...");
                            break;
                        case "admin":
                            System.out.println("Admin page coming soon...");
                            break;
                        default:
                            System.out.println("Unknown role.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Login failed due to system error.");
                e.printStackTrace();
            }
        }
    }

    private static void launchCustomerPage(User user, Scanner scanner) {
        CustomerProfileService profileService = new CustomerProfileService();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Customer Page ---");
            System.out.println("Welcome, " + user.getName() + "!");
            System.out.println("1. View Preferences & Allergies");
            System.out.println("2. Update Preferences & Allergies");
            System.out.println("3. View Past Orders (Coming Soon)");
            System.out.println("4. Create Custom Meal (Coming Soon)");
            System.out.println("5. Get AI Recipe Recommendation (Coming Soon)");
            System.out.println("6. Make an order (Coming Soon)");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    CustomerPreferences prefs = profileService.viewPreferences(user.getUserId());
                    System.out.println("Your Preferences:");
                    System.out.println("Dietary Preference: " + prefs.getDietaryPreference());
                    System.out.println("Allergy: " + prefs.getAllergy());
                    break;
                case "2":
                    System.out.print("Enter new dietary preference: ");
                    String newDiet = scanner.nextLine().trim();
                    System.out.print("Enter new allergy info: ");
                    String newAllergy = scanner.nextLine().trim();
                    try {
                        profileService.updatePreferences(user.getUserId(), newDiet, newAllergy);
                        System.out.println("Preferences updated successfully.");
                    } catch (Exception e) {
                        System.out.println("Failed to update preferences.");
                    }
                    break;
                case "3":
                case "4":
                case "5":
                case "6":
                    System.out.println("Feature coming soon...");
                    break;
                case "7":
                    running = false;
                    System.out.println("Logging out. Goodbye, " + user.getName() + "!");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
