package com.myproject.cooking1;

import com.myproject.cooking1.entities.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class MainProject {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        User user = null;

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
                    switch (user.getRole()) {
                        case "customer":
                            launchCustomerPage(user, scanner);
                            break;
                        case "chef":
                            launchChefPage(scanner);
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

    private static void launchCustomerPage(User user, Scanner scanner) throws SQLException {
        CustomerProfileService profileService = new CustomerProfileService();
        OrderService orderService = new OrderService();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Customer Page ---");
            System.out.println("Welcome, " + user.getName() + "!");
            System.out.println("1. View Preferences & Allergies");
            System.out.println("2. Update Preferences & Allergies");
            System.out.println("3. View Past Orders");
            System.out.println("4. Create Custom Meal (Coming Soon)");
            System.out.println("5. Get AI Recipe Recommendation (Coming Soon)");
            System.out.println("6. Make an order (Coming Soon)");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    CustomerPreferences prefs = profileService.viewPreferences(user.getUserId());
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
                    List<Map<String, String>> orders = orderService.getCustomerOrderHistory(user.getUserId());
                    if (orders.isEmpty()) {
                        System.out.println("You have no past orders.");
                    } else {
                        System.out.println("Your past orders:");
                        for (Map<String, String> order : orders) {
                            System.out.println("- " + order.get("name") +
                                    " | Price: $" + order.get("price") +
                                    " | Description: " + order.get("description"));
                        }
                    }
                    break;
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

    private static void launchChefPage(Scanner scanner) throws SQLException {
        OrderService orderService = new OrderService();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Chef Page ---");
            System.out.println("1. View Customer Order History");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.print("Enter customer user ID: ");
                    int customerId = Integer.parseInt(scanner.nextLine().trim());
                    List<Map<String, String>> orders = orderService.getCustomerOrderHistory(customerId);
                    if (orders.isEmpty()) {
                        System.out.println("No orders found for this customer.");
                    } else {
                        System.out.println("Customer's past orders:");
                        for (Map<String, String> order : orders) {
                            System.out.println("- " + order.get("name") +
                                    " | Price: $" + order.get("price") +
                                    " | Description: " + order.get("description"));
                        }
                    }
                    break;
                case "2":
                    running = false;
                    System.out.println("Logging out of chef page.");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
