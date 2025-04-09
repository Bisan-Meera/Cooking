package com.myproject.cooking1;

import com.myproject.cooking1.entities.ProfileForm;
import com.myproject.cooking1.entities.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static junit.framework.TestCase.assertEquals;

public class CustomerProfileAndPrefrences {

    @Given("the user is logged in")
    public void theUserIsLoggedIn() {
        // Placeholder
    }

    @Given("the Meals and Ingredients are defined in the system")
    public void theMealsAndIngredientsAreDefinedInTheSystem() {
        // Placeholder
    }

    @Given("a logged-in customer with user_id {int} and name {string} is on the profile settings page")
    public void aLoggedInCustomerWithUserIdAndNameIsOnTheProfileSettingsPage(Integer userId, String name) {
        System.out.println("Customer with ID " + userId + " is on profile settings page.");
    }

    @Given("a logged-in customer with user_id {int} is on the preferences page")
    public void aLoggedInCustomerWithUserIdIsOnThePreferencesPage(Integer userId) {
        System.out.println("Customer with ID " + userId + " is on preferences page.");
    }

    @When("they select {string} and {string} as preferences")
    public void theySelectAndAsPreferences(String pref1, String pref2) {
        // Placeholder for setting preferences
    }

    @Then("these preferences should be saved to their profile")
    public void thesePreferencesShouldBeSavedToTheirProfile() {
        // Placeholder
    }

    @Then("future meal suggestions should match these preferences")
    public void futureMealSuggestionsShouldMatchThesePreferences() {
        // Placeholder
    }

    @Given("the Users table contains customers")
    public void theUsersTableContainsCustomers() {
        // Placeholder
    }

    @Given("a logged-in customer with user_id {int} has previously saved dietary preferences")
    public void aLoggedInCustomerWithUserIdHasPreviouslySavedDietaryPreferences(Integer userId) {
        // Placeholder
    }

    @When("they visit the preferences tab")
    public void theyVisitThePreferencesTab() {
        // Placeholder
    }

    @Then("the saved preferences should be pre-filled and visible")
    public void theSavedPreferencesShouldBePreFilledAndVisible() {
        // Placeholder
    }

    @Given("a logged-in customer with user_id {int} has previously entered {string}")
    public void aLoggedInCustomerWithUserIdHasPreviouslyEntered(Integer userId, String allergy) {
        // Placeholder
    }

    @When("they update the allergy information to {string}")
    public void theyUpdateTheAllergyInformationTo(String allergy) {
        // Placeholder
    }

    @Then("their profile should reflect the updated allergy")
    public void theirProfileShouldReflectTheUpdatedAllergy() {
        // Placeholder
    }

    @Then("future meal suggestions should exclude meals containing seafood ingredients")
    public void futureMealSuggestionsShouldExcludeMealsContainingSeafoodIngredients() {
        // Placeholder
    }

    @When("they leave the preferences fields empty and submit the form")
    public void theyLeaveThePreferencesFieldsEmptyAndSubmitTheForm() {
        ProfileForm form = new ProfileForm();
        form.setPreferences(""); // empty
        String result = form.submit(); // returns "Preferences cannot be empty"
        TestContext.set("lastMessage", result); // ✅ Use consistent key
    }

    @Given("a logged-in customer with user_id {int} is on the profile settings page")
    public void aLoggedInCustomerWithUserIdIsOnTheProfileSettingsPage(Integer int1) {

    }

    @When("they select dietary preferences and a database error occurs while saving")
    public void theySelectDietaryPreferencesAndADatabaseErrorOccursWhileSaving() {
        ProfileForm form = new ProfileForm();
        form.setPreferences("Vegetarian"); // Set valid input to pass empty check
        form.simulateDbFailure(true);      // You'd need a method to force failure in the backend
        String result = form.submit();     // Should return system error message
        TestContext.set("lastMessage", result);
    }



}
