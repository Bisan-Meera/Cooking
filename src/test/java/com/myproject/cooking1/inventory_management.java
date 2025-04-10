package com.myproject.cooking1;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
public class inventory_management {
    @Given("the following ingredients exist:")
    public void theFollowingIngredientsExist(io.cucumber.datatable.DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.

    }
    @Given("2kg of chicken was used for an order")
    public void kgOfChickenWasUsedForAnOrder() {

    }
    @When("the meal is marked as prepared")
    public void theMealIsMarkedAsPrepared() {

    }
    @Then("the stock of chicken should reduce by 2kg")
    public void theStockOfChickenShouldReduceBy2kg() {

    }
    @Then("the new stock should be {int}.00kg")
    public void theNewStockShouldBe00kg(Integer int1) {

    }

    @Given("only 1kg of onions is left and the threshold is 2kg")
    public void only1kgOfOnionsIsLeftAndTheThresholdIs2kg() {

    }
    @When("another meal uses {int}.5kg of onions")
    public void anotherMealUses5kgOfOnions(Integer int1) {

    }
    @Then("the system should trigger a low-stock alert for {string}")
    public void theSystemShouldTriggerALowStockAlertFor(String string) {

    }

    @Given("kitchen staff opens the inventory page")
    public void kitchenStaffOpensTheInventoryPage() {

    }
    @When("the page loads")
    public void thePageLoads() {

    }
    @Then("it should list each ingredient with its current quantity and threshold")
    public void itShouldListEachIngredientWithItsCurrentQuantityAndThreshold() {

    }
    @Then("highlight any ingredients below threshold")
    public void highlightAnyIngredientsBelowThreshold() {
        ;
    }


}
