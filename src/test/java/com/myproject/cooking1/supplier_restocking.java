package com.myproject.cooking1;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
public class supplier_restocking {
    @Given("the following supplier exists:")
    public void theFollowingSupplierExists(io.cucumber.datatable.DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.

    }
    @Given("a supplier {string} is added")
    public void aSupplierIsAdded(String string) {

    }
    @When("the admin links them with {string} at {double} per unit")
    public void theAdminLinksThemWithAtPerUnit(String string, Double double1) {

    }
    @Then("the ingredient should be linked in the database")
    public void theIngredientShouldBeLinkedInTheDatabase() {

    }
    @Then("their pricing and supply data should be saved")
    public void theirPricingAndSupplyDataShouldBeSaved() {

    }


    @Given("the system detects {int} low-stock ingredients")
    public void theSystemDetectsLowStockIngredients(Integer int1) {

    }
    @When("the admin clicks {string}")
    public void theAdminClicks(String string) {

    }
    @Then("the system should create a suggested purchase list for those items")
    public void theSystemShouldCreateASuggestedPurchaseListForThoseItems() {

    }

    @Given("a supplier's phone number changed to {string}")
    public void aSupplierSPhoneNumberChangedTo(String string) {

    }
    @When("the admin updates Al-Madina Fresh's contact info")
    public void theAdminUpdatesAlMadinaFreshSContactInfo() {

    }
    @Then("the change should be saved in the database")
    public void theChangeShouldBeSavedInTheDatabase() {

    }
    @Then("future purchase orders should use the updated contact info")
    public void futurePurchaseOrdersShouldUseTheUpdatedContactInfo() {

    }




}
