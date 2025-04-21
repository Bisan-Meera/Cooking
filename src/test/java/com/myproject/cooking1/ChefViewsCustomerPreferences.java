package com.myproject.cooking1;
import com.myproject.cooking1.entities.ChefView;
import com.myproject.cooking1.entities.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ChefViewsCustomerPreferences {
    @Given("a customer has placed an order")
    public void aCustomerHasPlacedAnOrder() {

    }
    @Given("the customer has {string} preference and is allergic to {string}")
    public void theCustomerHasPreferenceAndIsAllergicTo(String string, String string2) {

    }
    @When("the chef opens the order details")
    public void theChefOpensTheOrderDetails() {

    }
    @Then("the customer's preferences and allergies should be visible")
    public void theCustomerSPreferencesAndAllergiesShouldBeVisible() {

    }
    @Given("multiple customers have placed orders")
    public void multipleCustomersHavePlacedOrders() {

    }
    @When("the chef opens the order list")
    public void theChefOpensTheOrderList() {

    }
    @Then("each order should display the corresponding customer's dietary preferences and allergies")
    public void eachOrderShouldDisplayTheCorrespondingCustomerSDietaryPreferencesAndAllergies() {

    }
    @Given("a customer has placed an order without saving any preferences")
    public void aCustomerHasPlacedAnOrderWithoutSavingAnyPreferences() {
        ChefView view = new ChefView();
        view.loadPreferences(""); // simulate missing preference
        TestContext.set("chefView", view);

        // Save the actual display result to 'lastMessage' so the shared step can use it
        String result = view.getDisplayedPreference(); // this will return "No preferences specified"
        TestContext.set("lastMessage", result); // ✅ this is the key used by the systemShouldShow() step
    }





}
