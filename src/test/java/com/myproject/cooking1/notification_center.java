package com.myproject.cooking1;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.myproject.cooking1.entities.NotificationService;

public class notification_center {
    private NotificationService notificationService = new NotificationService();
    private final int chefId = 3;
    private final int customerId = 1;
    private final int kitchenStaffId = 4;

    @Given("the stock of {string} drops below threshold")
    public void theStockOfDropsBelowThreshold(String ingredientName) {
        System.out.println("Stock of " + ingredientName + " dropped below threshold.");
    }
    @When("the system updates inventory")
    public void theSystemUpdatesInventory() {
        System.out.println("Inventory updated.");
    }
    @Then("a notification should be sent to all kitchen staff members")
    public void aNotificationShouldBeSentToAllKitchenStaffMembers() {
        String content = "Stock alert: Ingredient is below threshold.";
        notificationService.createNotification(kitchenStaffId, content);
    }
    @Given("a meal is prepared and packed")
    public void aMealIsPreparedAndPacked() {
        System.out.println("Meal prepared.");
    }
    @When("the chef marks it as {string}")
    public void theChefMarksItAs(String status) {
        if ("Ready".equalsIgnoreCase(status)) {
            System.out.println("Meal status set to Ready.");
        }
    }
    @Then("the customer should receive a notification with pickup\\/delivery info")
    public void theCustomerShouldReceiveANotificationWithPickupDeliveryInfo() {
        String content = "Your meal is ready for pickup or will be delivered shortly.";
        notificationService.createNotification(customerId, content);
    }
    @Given("a chef has a pending task due in {int} minutes")
    public void aChefHasAPendingTaskDueInMinutes(Integer minutes) {
        System.out.println("Chef has a task due in " + minutes + " minutes.");
    }
    @When("the system checks for upcoming tasks")
    public void theSystemChecksForUpcomingTasks() {
        System.out.println("Checking tasks...");

    }
    @Then("it sends a reminder alert to the chef")
    public void itSendsAReminderAlertToTheChef() {
        String content = "Reminder: You have a task due soon.";
        notificationService.createNotification(chefId, content);
    }
    @Given("an ingredient is used in an order and its quantity drops below threshold")
    public void anIngredientIsUsedInAnOrderAndItsQuantityDropsBelowThreshold() {
        System.out.println("Ingredient used in order.");
    }

    @When("the stock update is processed")
    public void theStockUpdateIsProcessed() {
        System.out.println("Stock update logic complete.");
    }

    @Then("the system creates a restocking notification")
    public void theSystemCreatesARestockingNotification() {
        String content = "Restocking suggestion: Ingredient stock is low.";
        notificationService.createNotification(kitchenStaffId, content);
    }

    @Then("marks it as unread with the current timestamp")
    public void marksItAsUnreadWithTheCurrentTimestamp() {
        System.out.println("Notification marked as unread.");
    }

    @Given("a meal is scheduled for delivery in less than {int} hour")
    public void aMealIsScheduledForDeliveryInLessThanHour(Integer int1) {
        System.out.println("Meal scheduled within " + int1 + " hour(s).");
    }

    @When("the system checks upcoming deliveries")
    public void theSystemChecksUpcomingDeliveries() {
        System.out.println("System checking delivery times.");
    }

    @Then("the customer should receive a reminder notification")
    public void theCustomerShouldReceiveAReminderNotification() {
        String content = "Reminder: Your meal will be delivered soon.";
        notificationService.createNotification(customerId, content);
    }
}
