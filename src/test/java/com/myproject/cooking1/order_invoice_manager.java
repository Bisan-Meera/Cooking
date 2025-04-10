package com.myproject.cooking1;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
public class order_invoice_manager {
    @Given("the following customer exists:")
    public void theFollowingCustomerExists(io.cucumber.datatable.DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.

    }
    @Given("customer Layla Hassan adds {int} meals to the cart")
    public void customerLaylaHassanAddsMealsToTheCart(Integer int1) {

    }
    @When("she proceeds to checkout")
    public void sheProceedsToCheckout() {

    }
    @Then("a new order and corresponding invoice should be generated")
    public void aNewOrderAndCorrespondingInvoiceShouldBeGenerated() {

    }

    @Given("Layla Hassan placed an order last week")
    public void laylaHassanPlacedAnOrderLastWeek() {

    }
    @When("she checks the invoice tab")
    public void sheChecksTheInvoiceTab() {

    }
    @Then("she should be able to view and download the invoice")
    public void sheShouldBeAbleToViewAndDownloadTheInvoice() {

    }

    @Given("multiple orders were completed today")
    public void multipleOrdersWereCompletedToday() {

    }
    @When("the admin clicks on “Daily Summary”")
    public void theAdminClicksOnDailySummary() {

    }
    @Then("a report showing total revenue and meal counts should be generated")
    public void aReportShowingTotalRevenueAndMealCountsShouldBeGenerated() {

    }


}
