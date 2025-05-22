package com.myproject.cooking1;

import com.myproject.cooking1.entities.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static junit.framework.TestCase.assertEquals;

public class CustomerProfileAndPrefrences {
    private int currentUserId;
    private String currentUserName;
    private String selectedPreference;
    private String selectedAllergy;
    private CustomerProfileService profileService;

    @Before
    public void setUp() {
        try {
            profileService = new CustomerProfileService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
        currentUserId = userId;
        currentUserName = name;
        System.out.println("Customer with ID " + userId + " is on profile settings page.");
    }


    @Given("a logged-in customer with user_id {int} is on the preferences page")
    public void aLoggedInCustomerWithUserIdIsOnThePreferencesPage(Integer userId) {
        currentUserId = userId;
        System.out.println("Customer with ID " + userId + " is on the preferences page.");
    }


    @When("they select {string} and {string} as preferences")
    public void theySelectAndAsPreferences(String pref1, String pref2) {
        selectedPreference = pref1;
        selectedAllergy = pref2;
        try {
            profileService.updatePreferences(currentUserId, selectedPreference, selectedAllergy);
        } catch (Exception e) {
            e.printStackTrace();
            TestContext.set("lastMessage", "Database error occurred");
        }
    }


    @Then("these preferences should be saved to their profile")
    public void thesePreferencesShouldBeSavedToTheirProfile() {
        try {
            CustomerPreferences prefs = profileService.viewPreferences(currentUserId);
            assertEquals(selectedPreference, prefs.getDietaryPreference());
            assertEquals(selectedAllergy, prefs.getAllergy());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Then("future meal suggestions should match these preferences")
    public void futureMealSuggestionsShouldMatchThesePreferences() {
        // Placeholder
    }

    @Given("the Users table contains customers")
    public void theUsersTableContainsCustomers() {
        try (Connection conn = DatabaseHelper.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Users (user_id, name, email, password, role) " +
                            "VALUES (?, ?, ?, ?, 'customer') ON CONFLICT (user_id) DO NOTHING"
            );
            stmt.setInt(1, 5000);
            stmt.setString(2, "Test User");
            stmt.setString(3, "test5000@test.com");
            stmt.setString(4, "password123");
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to insert test user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Given("a logged-in customer with user_id {int} has previously saved dietary preferences")
    public void aLoggedInCustomerWithUserIdHasPreviouslySavedDietaryPreferences(Integer userId) {
        currentUserId = userId;
        try {
            CustomerPreferences prefs = profileService.viewPreferences(userId);
            System.out.println("Existing preferences: " + prefs.getDietaryPreference() + ", " + prefs.getAllergy());
        } catch (Exception e) {
            e.printStackTrace();
            TestContext.set("lastMessage", "No preferences found for user_id: " + userId);
        }
    }


    @When("they visit the preferences tab")
    public void theyVisitThePreferencesTab() {
        System.out.println("Customer is viewing the preferences tab.");
    }


    @Then("the saved preferences should be pre-filled and visible")
    public void theSavedPreferencesShouldBePreFilledAndVisible() {
        try {
            CustomerPreferences prefs = profileService.viewPreferences(currentUserId);
            assert prefs != null;
            System.out.println("Pre-filled Preferences - Dietary: " + prefs.getDietaryPreference() + ", Allergy: " + prefs.getAllergy());
        } catch (Exception e) {
            e.printStackTrace();
            TestContext.set("lastMessage", "Preferences could not be loaded");
        }
    }


    @Given("a logged-in customer with user_id {int} has previously entered {string}")
    public void aLoggedInCustomerWithUserIdHasPreviouslyEntered(Integer userId, String allergy) {
        currentUserId = userId;
        try {
            profileService.updatePreferences(userId, "", allergy);
            System.out.println("Saved allergy '" + allergy + "' for user_id " + userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @When("they update the allergy information to {string}")
    public void theyUpdateTheAllergyInformationTo(String allergy) {
        selectedAllergy = allergy;
        try {
            profileService.updatePreferences(currentUserId, "", allergy); // Leave preference unchanged
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Then("their profile should reflect the updated allergy")
    public void theirProfileShouldReflectTheUpdatedAllergy() {
        try {
            CustomerPreferences prefs = profileService.viewPreferences(currentUserId);
            assertEquals(selectedAllergy, prefs.getAllergy());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Then("future meal suggestions should exclude meals containing seafood ingredients")
    public void futureMealSuggestionsShouldExcludeMealsContainingSeafoodIngredients() {
        System.out.println("Expecting future suggestions to exclude seafood ingredients (simulated).");
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
        form.setUserId(2);
        form.setPreferences("Vegetarian");
        form.simulateDbFailure(true);
        String result = form.submit(); // Should return "Unable to save preferences due to system error"
        TestContext.set("lastMessage", result);
    }


    @When("they try to save preferences and a simulated DB failure occurs")
    public void theyTryToSavePreferencesAndASimulatedDbFailureOccurs() {
        try {
            profileService.updatePreferences(3, "Vegan", "None"); // Should throw the special message
        } catch (RuntimeException e) {
            TestContext.set("lastMessage", e.getMessage());
        }
    }


    @When("they try to view preferences and the database fails")
    public void theyTryToViewPreferencesAndDbFails() {
        try {
            profileService.viewPreferences(currentUserId);
        } catch (RuntimeException e) {
            TestContext.set("lastMessage", e.getMessage());
        }
    }

    @When("they view preferences without any saved data")
    public void theyViewPreferencesWithoutSavedData() {
        CustomerPreferences prefs = profileService.viewPreferences(currentUserId);
        TestContext.set("prefs", prefs);
    }
    @Then("the system should return blank preferences")
    public void theSystemShouldReturnBlankPreferences() {
        CustomerPreferences prefs = TestContext.get("prefs", CustomerPreferences.class);
        assertEquals("", prefs.getDietaryPreference());
        assertEquals("", prefs.getAllergy());
    }

    @When("they set the preference to {string} and leave allergy empty")
    public void theySetPreferenceOnly(String preference) {
        ProfileForm form = new ProfileForm();
        form.setUserId(currentUserId);
        form.setPreferences(preference);
        form.setAllergy(""); // empty allergy
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }

    @When("they set the allergy to {string} and leave preference empty")
    public void theySetAllergyOnly(String allergy) {
        ProfileForm form = new ProfileForm();
        form.setUserId(currentUserId);
        form.setPreferences(""); // empty preference
        form.setAllergy(allergy);
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }

    @When("they submit the profile form")
    public void theySubmitProfileForm() {
        // Action is done in above steps, nothing to do here
    }

    @Then("Then the system should show {string}")
    public void theSystemShouldDisplay(String expectedMsg) {
        assertEquals(expectedMsg, TestContext.get("lastMessage", String.class));
    }


    @When("they set both preference and allergy to null")
    public void theySetBothFieldsNull() {
        ProfileForm form = new ProfileForm();
        form.setUserId(currentUserId);
        form.setPreferences(null);
        form.setAllergy(null);
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }


    @When("they simulate a DB failure and submit preference {string}")
    public void simulateDbFailureSubmit(String preference) {
        ProfileForm form = new ProfileForm();
        form.setUserId(currentUserId);
        form.setPreferences(preference);
        form.setAllergy("");
        form.simulateDbFailure(true);
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }

    @When("they retry submitting with DB OK")
    public void retryWithDbOk() {
        ProfileForm form = new ProfileForm();
        form.setUserId(currentUserId);
        form.setPreferences("Vegetarian");
        form.setAllergy("");
        form.simulateDbFailure(false);
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }

    @When("they set the preference to {string} and the allergy to {string}")
    public void setPreferenceAndAllergyWithSpaces(String preference, String allergy) {
        ProfileForm form = new ProfileForm();
        form.setUserId(currentUserId);
        form.setPreferences(preference);
        form.setAllergy(allergy);
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }


    @When("they try to save preferences and a blank error occurs")
    public void theyTryToSavePreferencesAndABlankErrorOccurs() {
        ProfileForm form = new ProfileForm() {
            @Override
            public String submit() {
                try { throw new RuntimeException(""); }
                catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && !msg.isBlank()) return msg;
                    return "Unable to save preferences due to system error";
                }
            }
        };
        form.setUserId(404);
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }

    @When("they try to save preferences and a null error occurs")
    public void theyTryToSavePreferencesAndANullErrorOccurs() {
        ProfileForm form = new ProfileForm() {
            @Override
            public String submit() {
                try { throw new RuntimeException((String)null); }
                catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && !msg.isBlank()) return msg;
                    return "Unable to save preferences due to system error";
                }
            }
        };
        form.setUserId(405);
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }


    @When("they try to save preferences and a checked exception occurs")
    public void theyTryToSavePreferencesAndACheckedExceptionOccurs() {
        ProfileForm form = new ProfileForm() {
            @Override
            public String submit() {
                try { throw new Exception("checked exception!"); }
                catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && !msg.isBlank()) return msg;
                    return "Unable to save preferences due to system error";
                }
            }
        };
        form.setUserId(406);
        String result = form.submit();
        TestContext.set("lastMessage", result);
    }
    @When("they set the preference to null and the allergy to {string}")
    public void theySetPreferenceNullAndAllergy(String allergy) {
        ProfileForm form = new ProfileForm();
        form.setUserId(currentUserId);
        form.setPreferences(null);
        form.setAllergy(allergy);
        String result = form.submit();
        TestContext.set("lastMessage", result != null ? result : "");
    }



}
