package com.myproject.cooking1;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CustomerProfileAndPrefrences {
    private String lastMessage = "";

    @Given("the user is logged in")
    public void theUserIsLoggedIn() {

    }



    @Given("the Meals and Ingredients are defined in the system")
    public void theMealsAndIngredientsAreDefinedInTheSystem() {

    }
    @Given("a logged-in customer with user_id {int} and name {string} is on the profile settings page")
    public void aLoggedInCustomerWithUserIdAndNameIsOnTheProfileSettingsPage(Integer int1, String string) {

    }
    @When("they select {string} and {string} as preferences")
    public void theySelectAndAsPreferences(String string, String string2) {

    }
    @Then("these preferences should be saved to their profile")
    public void thesePreferencesShouldBeSavedToTheirProfile() {

    }
    @Then("future meal suggestions should match these preferences")
    public void futureMealSuggestionsShouldMatchThesePreferences() {

    }

    @Given("the Users table contains customers")
    public void theUsersTableContainsCustomers() {

    }

    @Given("a logged-in customer with user_id {int} has previously saved dietary preferences")
    public void aLoggedInCustomerWithUserIdHasPreviouslySavedDietaryPreferences(Integer int1) {

    }
    @When("they visit the preferences tab")
    public void theyVisitThePreferencesTab() {

    }
    @Then("the saved preferences should be pre-filled and visible")
    public void theSavedPreferencesShouldBePreFilledAndVisible() {

    }


    @Given("a logged-in customer with user_id {int} has previously entered {string}")
    public void aLoggedInCustomerWithUserIdHasPreviouslyEntered(Integer int1, String string) {

    }
    @When("they update the allergy information to {string}")
    public void theyUpdateTheAllergyInformationTo(String string) {

    }
    @Then("their profile should reflect the updated allergy")
    public void theirProfileShouldReflectTheUpdatedAllergy() {

    }
    @Then("future meal suggestions should exclude meals containing seafood ingredients")
    public void futureMealSuggestionsShouldExcludeMealsContainingSeafoodIngredients() {

    }











}
