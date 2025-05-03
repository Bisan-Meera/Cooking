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
                            launchAdminPage(scanner);
                            break;
                        case "kitchen_staff":
                            launchKitchenManagerPage(scanner, user);
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
    private static void launchKitchenManagerPage(Scanner scanner, User user) throws SQLException {
        IngredientStockService stockService = new IngredientStockService();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Kitchen Manager Page ---");
            System.out.println("Welcome, " + user.getName() + "!");
            System.out.println("1. View Low Stock Ingredients");
            System.out.println("2. Update Stock Manually");
            System.out.println("3. Trigger Restocking Suggestions");
            System.out.println("4. Manage Purchase Orders");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    List<String> lowStock = stockService.getLowStockIngredients();
                    if (lowStock.isEmpty()) {
                        System.out.println("All ingredients are above threshold.");
                    } else {
                        System.out.println("Low stock ingredients:");
                        lowStock.forEach(name -> System.out.println(" - " + name));
                    }
                    break;

                case "2":
                    System.out.print("Enter Ingredient ID to update: ");
                    int ingId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter new quantity: ");
                    double qty = Double.parseDouble(scanner.nextLine().trim());
                    stockService.updateIngredientStock(ingId, qty);
                    System.out.println("Stock updated for ingredient ID " + ingId);
                    break;

                case "3":
                    List<String> toRestock = stockService.getLowStockIngredients();
                    if (toRestock.isEmpty()) {
                        System.out.println("No restocking needed.");
                    } else {
                        NotificationService notifier = new NotificationService();
                        for (String name : toRestock) {
                            String content = "Restock suggestion for " + name;
                            notifier.createNotification(user.getUserId(), content);
                        }
                        System.out.println("Restocking suggestions sent.");
                    }
                    break;
                case "4":
                    PurchaseOrderService poService = new PurchaseOrderService();
                    poService.openOrderSubMenu(scanner);
                    break;

                case "5":
                    running = false;
                    System.out.println("Logging out. Goodbye, " + user.getName() + "!");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
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
            System.out.println("4. Create Custom Meal");
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
                    List<String> ingredients = new ArrayList<>();
                    System.out.println("Enter ingredients for your custom meal (type 'done' to finish):");
                    while (true) {
                        String ing = scanner.nextLine().trim();
                        if (ing.equalsIgnoreCase("done")) break;
                        ingredients.add(ing);
                    }
                    try {
                        boolean result = CustomOrderService.submitCustomMeal(user.getUserId(), ingredients);
                        if (result) {
                            System.out.println("Custom meal created successfully!");
                        } else {
                            System.out.println("Failed to create custom meal.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
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
        CustomerProfileService profileService = new CustomerProfileService();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Chef Page ---");
            System.out.println("1. View Customer Order History");
            System.out.println("2. View Customer Preferences & Allergies");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.print("Enter customer user ID: ");
                    int customerId = Integer.parseInt(scanner.nextLine().trim());

                    try (Connection conn = DBConnection.getConnection()) {
                        User targetUser = User.getUserById(customerId, conn);
                        if (targetUser == null || !targetUser.getRole().equals("customer")) {
                            System.out.println("No customer found with this ID.");
                        } else {
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
                        }
                    } catch (Exception e) {
                        System.out.println("Error fetching customer order history.");
                        e.printStackTrace();
                    }
                    break;

                case "2":
                    System.out.print("Enter customer user ID: ");
                    int customerIdForPrefs = Integer.parseInt(scanner.nextLine().trim());

                    try {
                        CustomerPreferences prefs = profileService.viewPreferences(customerIdForPrefs);
                        if (prefs != null) {
                            System.out.println("Customer Preferences:");
                            System.out.println("Dietary Preference: " + prefs.getDietaryPreference());
                            System.out.println("Allergy: " + prefs.getAllergy());
                        } else {
                            System.out.println("No preferences found for this customer.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error retrieving preferences.");
                        e.printStackTrace();
                    }
                    break;

                case "3":
                    running = false;
                    System.out.println("Logging out of chef page.");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void launchAdminPage(Scanner scanner) throws SQLException {
        AdminService adminService = new AdminService();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Admin Page ---");
            System.out.println("1. View All Customer Orders");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    Map<Integer, List<String>> allOrders = adminService.getAllCustomerOrders();
                    if (allOrders.isEmpty()) {
                        System.out.println("No customer orders found.");
                    } else {
                        System.out.println("All Customer Orders:");
                        allOrders.forEach((id, meals) -> {
                            System.out.println("Customer ID " + id + ":");
                            for (String meal : meals) {
                                System.out.println("  - " + meal);
                            }
                        });
                    }
                    break;

                case "2":
                    running = false;
                    System.out.println("Logging out of admin page.");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

}