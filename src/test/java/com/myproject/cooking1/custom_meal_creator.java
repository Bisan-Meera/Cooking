package com.myproject.cooking1;


import com.myproject.cooking1.entities.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static junit.framework.TestCase.assertEquals;

public class custom_meal_creator {

    @Given("the Ingredients table has current stock levels")
    public void theIngredientsTableHasCurrentStockLevels() {

    }
    @Given("the user is logged in as a customer")
    public void theUserIsLoggedInAsACustomer() {

    }
    @Given("a customer selects chicken, rice, and broccoli")
    public void aCustomerSelectsChickenRiceAndBroccoli() {

    }
    @When("they submit the custom meal request")
    public void theySubmitTheCustomMealRequest() {

    }
    @Then("the system should accept and save the customized order")
    public void theSystemShouldAcceptAndSaveTheCustomizedOrder() {

    }
    @Then("deduct the used ingredient quantities from stock")
    public void deductTheUsedIngredientQuantitiesFromStock() {

    }


    @Given("the ingredient {string} is currently out of stock")
    public void theIngredientIsCurrentlyOutOfStock(String string) {

    }
    @When("a customer tries to add {string} to their custom meal")
    public void aCustomerTriesToAddToTheirCustomMeal(String string) {

    }
    @Then("the system should notify {string}")
    public void theSystemShouldNotify(String string) {

    }
    @Then("suggest an available alternative ingredient")
    public void suggestAnAvailableAlternativeIngredient() {

    }

    @Given("the customer is allergic to dairy")
    public void theCustomerIsAllergicToDairy() {

    }
    @When("they try to add {string} to their custom meal")
    public void theyTryToAddToTheirCustomMeal(String string) {

    }
    @Then("the system should block the selection")
    public void theSystemShouldBlockTheSelection() {

    }
    @Then("show a warning {string}")
    public void showAWarning(String string) {

    }

    @Given("a customer is on the custom meal creator page")
    public void aCustomerIsOnTheCustomMealCreatorPage() {

    }
    @When("they submit the form without selecting any ingredients")
    public void theySubmitTheFormWithoutSelectingAnyIngredients() {
        // Simulate validation error
        TestContext.set("lastMessage", "At least one ingredient must be selected");
    }


    @Given("a customer selects ingredients for a custom meal")
    public void aCustomerSelectsIngredientsForACustomMeal() {

    }
    @When("a database error occurs during save")
    public void aDatabaseErrorOccursDuringSave() {
        // Simulate failure to save due to DB error
        TestContext.set("lastMessage", "Failed to save custom meal due to system error");
    }




}
