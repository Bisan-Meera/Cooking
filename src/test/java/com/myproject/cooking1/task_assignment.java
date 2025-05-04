package com.myproject.cooking1;
import com.myproject.cooking1.entities.DatabaseHelper;
import com.myproject.cooking1.entities.TaskAssignmentService;
import com.myproject.cooking1.entities.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.SQLException;

import static junit.framework.TestCase.assertEquals;
public class task_assignment {
    @Given("the system has chefs with different workloads and expertise levels")
    public void theSystemHasChefsWithDifferentWorkloadsAndExpertiseLevels() {
        // 🧹 No need to add new chefs anymore, just clear tasks
        DatabaseHelper.clearChefsTasksOnly();  // We'll add this helper below

        int mariaId = DatabaseHelper.getChefIdByName("Chef Maria");
        int lucaId = DatabaseHelper.getChefIdByName("Chef Luca");

        DatabaseHelper.setChefTaskCount(mariaId, 2);
        DatabaseHelper.setChefTaskCount(lucaId, 1);
    }




    @Given("there are pending cooking tasks in the kitchen")
    public void thereArePendingCookingTasksInTheKitchen() {

    }
    @When("the kitchen manager assigns a new cooking task")
    public void theKitchenManagerAssignsANewCookingTask() throws SQLException {
        int expectedChefId = TaskAssignmentService.findLeastLoadedChef();
        int taskId = TaskAssignmentService.assignToLeastLoadedChef();

        TestContext.set("assignedTaskId", taskId);
        TestContext.set("expectedChefId", expectedChefId);
    }


    @Then("the task should be assigned to the chef with the least workload")
    public void theTaskShouldBeAssignedToTheChefWithTheLeastWorkload() throws SQLException {
        int taskId = TestContext.get("assignedTaskId", Integer.class);
        int assignedChefId = TaskAssignmentService.getAssignedChef(taskId);
        int expectedChefId = TestContext.get("expectedChefId", Integer.class);

        assertEquals(expectedChefId, assignedChefId);
    }





    @Given("a cooking task requires {string} cuisine expertise")
    public void aCookingTaskRequiresCuisineExpertise(String cuisine) {
        TestContext.set("requiredExpertise", cuisine);
    }

    @When("the kitchen manager assigns the task")
    public void theKitchenManagerAssignsTheTask() {
        String cuisine = TestContext.get("requiredExpertise", String.class);
        int taskId = TaskAssignmentService.assignToChefWithExpertise(cuisine);
        TestContext.set("assignedTaskId", taskId);
    }

    @Then("the task should be assigned to a chef with {string} expertise")
    public void theTaskShouldBeAssignedToAChefWithExpertise(String expectedCuisine) throws SQLException {
        int taskId = TestContext.get("assignedTaskId", Integer.class);
        int chefId = TaskAssignmentService.getAssignedChef(taskId);
        String actualCuisine = TaskAssignmentService.getChefExpertise(chefId);

        assertEquals(expectedCuisine.toLowerCase(), actualCuisine.toLowerCase());
    }


    @When("a task is assigned to a chef")
    public void aTaskIsAssignedToAChef() {
        int taskId = TaskAssignmentService.assignToLeastLoadedChef();
        TestContext.set("assignedTaskId", taskId);
    }

    @Then("the chef should receive a notification about the task")
    public void theChefShouldReceiveANotificationAboutTheTask() throws SQLException {
        int taskId = TestContext.get("assignedTaskId", Integer.class);
        int chefId = TaskAssignmentService.getAssignedChef(taskId);
        boolean received = TaskAssignmentService.hasNotification(chefId, taskId);

        assertEquals(true, received);
    }


    @Given("Chef John already has {int} active tasks")
    public void chefJohnAlreadyHasActiveTasks(Integer count) {
        int chefId = DatabaseHelper.getChefIdByName("Chef John");
        DatabaseHelper.setChefTaskCount(chefId, count);
    }

    @When("the kitchen manager tries to assign a new task")
    public void theKitchenManagerTriesToAssignANewTask() {
        int taskId = TaskAssignmentService.assignToLeastLoadedChef();
        TestContext.set("assignedTaskId", taskId);
    }

    @Then("the task should be assigned to another available chef")
    public void theTaskShouldBeAssignedToAnotherAvailableChef() throws SQLException {
        int taskId = TestContext.get("assignedTaskId", Integer.class);
        int assignedChefId = TaskAssignmentService.getAssignedChef(taskId);
        int johnId = DatabaseHelper.getChefIdByName("Chef John");

        assertEquals(false, assignedChefId == johnId);
    }



}
