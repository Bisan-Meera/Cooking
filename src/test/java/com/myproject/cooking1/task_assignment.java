package com.myproject.cooking1;
import com.myproject.cooking1.entities.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static junit.framework.TestCase.assertEquals;
public class task_assignment {
    @Given("the following users exist:")
    public void theFollowingUsersExist(io.cucumber.datatable.DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.

    }

    @Given("the following orders exist:")
    public void theFollowingOrdersExist(io.cucumber.datatable.DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.

    }
    @Given("Fatima Ibrahim is logged in as kitchen manager")
    public void fatimaIbrahimIsLoggedInAsKitchenManager() {

    }
    @When("she assigns task {string} to chef Yasser for order {int}")
    public void sheAssignsTaskToChefYasserForOrder(String string, Integer int1) {

    }
    @Then("the task should be saved in the system")
    public void theTaskShouldBeSavedInTheSystem() {

    }
    @Then("it should appear on Yasser’s dashboard")
    public void itShouldAppearOnYasserSDashboard() {

    }

    @Given("chef Yasser has a task {string} for order {int} with status {string}")
    public void chefYasserHasATaskForOrderWithStatus(String string, Integer int1, String string2) {

    }
    @When("he marks the task as completed")
    public void heMarksTheTaskAsCompleted() {

    }
    @Then("the task status should be updated to {string}")
    public void theTaskStatusShouldBeUpdatedTo(String string) {

    }
    @Then("the update should be reflected on his task list")
    public void theUpdateShouldBeReflectedOnHisTaskList() {

    }

    @Given("a kitchen staff member is logged in")
    public void aKitchenStaffMemberIsLoggedIn() {

    }
    @When("they visit their tasks tab")
    public void theyVisitTheirTasksTab() {

    }
    @Then("all assigned tasks should be listed with status and order ID")
    public void allAssignedTasksShouldBeListedWithStatusAndOrderID() {

    }




}
