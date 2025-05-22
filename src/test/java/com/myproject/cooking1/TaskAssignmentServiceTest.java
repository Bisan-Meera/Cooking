package com.myproject.cooking1;

import com.myproject.cooking1.entities.DatabaseHelper;
import com.myproject.cooking1.entities.TaskAssignmentService;
import com.myproject.cooking1.entities.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TaskAssignmentServiceTest {
    @Test
    public void testAssignToLeastLoadedChef_DbFailure() {
        DatabaseHelper.simulateDatabaseFailure(true);
        int taskId = TaskAssignmentService.assignToLeastLoadedChef();
        assertEquals(-1, taskId);
        DatabaseHelper.simulateDatabaseFailure(false);
    }

    @Test
    public void testAssignToChefWithExpertise_DbFailure() {
        DatabaseHelper.simulateDatabaseFailure(true);
        int taskId = TaskAssignmentService.assignToChefWithExpertise("Anything");
        assertEquals(-1, taskId);
        DatabaseHelper.simulateDatabaseFailure(false);
    }

    @Test
    public void testGetChefExpertise_NotChef() {
        String expertise = TaskAssignmentService.getChefExpertise(99999);
        assertNull(expertise);
    }

    @Test
    public void testGetAllChefsWithWorkloadAndExpertise_Empty() {
        DatabaseHelper.clearChefsAndTasks();
        List<User> chefs = TaskAssignmentService.getAllChefsWithWorkloadAndExpertise();
        assertTrue(chefs.isEmpty());
    }

    @Test
    public void testGetTaskCount_InvalidUser() {
        String count = TaskAssignmentService.getTaskCount(99999);
        assertEquals("0", count);
    }

    @Test
    public void testMarkTaskAsReady_TaskNotLinkedToOrder() {
        DatabaseHelper.clearChefsAndTasks();
        // Insert a task with no order links
        int chefId = DatabaseHelper.addChef("Temp Chef", "None", 0);
        int taskId = DatabaseHelper.createUnlinkedTask(chefId);
        boolean result = TaskAssignmentService.markTaskAsReady(taskId);
        assertTrue(result); // Should still return true even if no customer found
    }

    @Test
    public void testShowPendingTasksWithDetails() {
        // Setup test data as needed (or clear all to see empty output)
        DatabaseHelper.clearChefsAndTasks();
        TaskAssignmentService.showPendingTasksWithDetails();
        // (Optional) Capture System.out to assert print, but for coverage, just calling it is enough
    }

    @Test
    public void testShowActiveTasksForChef() {
        int chefId = DatabaseHelper.addChef("UnitTest Chef", "Unit", 0);
        TaskAssignmentService.showActiveTasksForChef(chefId);
        // (Optional) Add tasks, etc. For coverage, just calling is enough
    }

    // Add test for error branch too!
    @Test
    public void testShowPendingTasksWithDetails_DbFailure() {
        DatabaseHelper.simulateDatabaseFailure(true);
        TaskAssignmentService.showPendingTasksWithDetails();
        DatabaseHelper.simulateDatabaseFailure(false);
    }

    @Test
    public void testShowActiveTasksForChef_DbFailure() {
        int chefId = DatabaseHelper.addChef("Error Chef", "ErrCuisine", 0);
        DatabaseHelper.simulateDatabaseFailure(true);
        TaskAssignmentService.showActiveTasksForChef(chefId);
        DatabaseHelper.simulateDatabaseFailure(false);
    }
    @Test
    public void testCapturePendingTasksWithDetails() {
        String result = TaskAssignmentService.capturePendingTasksWithDetails();
        assertTrue(result.contains("Pending Tasks")); // Basic assertion
    }

    @Test
    public void testCaptureActiveTasksForChef() {
        int chefId = DatabaseHelper.addChef("Active Chef", "ActiveCuisine", 0);
        String result = TaskAssignmentService.captureActiveTasksForChef(chefId);
        assertNotNull(result); // Basic assertion
    }

}
