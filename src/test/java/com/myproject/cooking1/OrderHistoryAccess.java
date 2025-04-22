package com.myproject.cooking1;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class OrderHistoryAccess {
    @Given("the customer {string} with user_id {int} has ordered {string}")
    public void theCustomerWithUserIdHasOrdered(String string, Integer int1, String string2) {

    }
    @When("she logs in and goes to her order history page")
    public void sheLogsInAndGoesToHerOrderHistoryPage() {

    }
    @Then("she should see {string} listed with price and description")
    public void sheShouldSeeListedWithPriceAndDescription(String string) {

    }

    @Given("the customer {string} with user_id {int} has no past orders")
    public void theCustomerWithUserIdHasNoPastOrders(String string, Integer int1) {

    }
    @When("he logs in and goes to the order history page")
    public void heLogsInAndGoesToTheOrderHistoryPage() {

    }
    @Then("he should see a message saying {string}")
    public void heShouldSeeAMessageSaying(String string) {

    }
    @Given("the chef {string} is logged in")
    public void theChefIsLoggedIn(String string) {

    }
    @Given("customer {string} has placed multiple orders")
    public void customerHasPlacedMultipleOrders(String string) {

    }
    @When("the chef selects {string} from the customer list")
    public void theChefSelectsFromTheCustomerList(String string) {

    }
    @Then("he should see all meals she has ordered in the past")
    public void heShouldSeeAllMealsSheHasOrderedInThePast() {

    }

    @Given("the admin {string} is logged in")
    public void theAdminIsLoggedIn(String string) {

    }
    @When("she accesses the system order analytics dashboard")
    public void sheAccessesTheSystemOrderAnalyticsDashboard() {

    }
    @Then("she should be able to retrieve all orders placed by all customers")
    public void sheShouldBeAbleToRetrieveAllOrdersPlacedByAllCustomers() {

    }




}
