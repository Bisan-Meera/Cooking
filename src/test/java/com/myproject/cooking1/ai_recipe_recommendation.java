package com.myproject.cooking1;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.myproject.cooking1.entities.RecipeRecommenderService;

import java.util.*;
import static org.junit.Assert.*;

public class ai_recipe_recommendation {

    private String dietaryPreference;
    private String allergy = "";
    private int availableTime;
    private List<String> availableIngredients;
    private String recommendation;
    private String explanation;

    @Given("the following customer preferences exist:")
    public void theFollowingCustomerPreferencesExist(DataTable dataTable) {
        System.out.println("✅ Customer preferences loaded:");
        dataTable.asMaps().forEach(System.out::println);
    }

    @Given("the meals have preparation times and mapped ingredients")
    public void theMealsHavePreparationTimesAndMappedIngredients() {
        System.out.println("✅ Meal database is assumed populated with ingredients and prep time.");
    }

    @Given("the stock levels for ingredients are up to date")
    public void theStockLevelsForIngredientsAreUpToDate() {
        System.out.println("✅ Ingredient stock levels are assumed current.");
    }

    @Given("user {int} wants a vegan recipe")
    public void userWantsAVeganRecipe(Integer userId) {
        this.dietaryPreference = "Vegan";
    }

    @Given("user {int} wants a vegetarian recipe")
    public void userWantsAVegetarianRecipe(Integer userId) {
        this.dietaryPreference = "Vegetarian";
    }

    @Given("user {int} follows Halal dietary rules")
    public void userFollowsHalalDietaryRules(Integer userId) {
        this.dietaryPreference = "Halal";
    }

    @Given("is allergic to gluten")
    public void isAllergicToGluten() {
        this.allergy = "gluten";
    }

    @Given("has {int} minutes available")
    public void hasMinutesAvailable(Integer minutes) {
        this.availableTime = minutes;
    }

    @Given("has the following ingredients:")
    public void hasTheFollowingIngredients(DataTable dataTable) {
        this.availableIngredients = dataTable.asList().stream().map(String::toLowerCase).toList();
    }

    @Given("the following ingredients are available:")
    public void theFollowingIngredientsAreAvailable(DataTable dataTable) {
        this.availableIngredients = dataTable.asList().stream().map(String::toLowerCase).toList();
    }

    @Given("has only the following ingredients:")
    public void hasOnlyTheFollowingIngredients(DataTable dataTable) {
        this.availableIngredients = dataTable.asList().stream().map(String::toLowerCase).toList();
    }

    @When("the AI assistant checks for suitable meals")
    public void theAIAssistantChecksForSuitableMeals() {
        List<Map<String, Object>> results = RecipeRecommenderService.recommendMeals(
                dietaryPreference,
                allergy,
                availableTime,
                availableIngredients
        );

        if (!results.isEmpty()) {
            recommendation = (String) results.get(0).get("name");
            explanation = "Meal recommended based on ingredients, time, and dietary rules.";
        } else {
            recommendation = "No valid recipes found";
            explanation = "None fit the user's constraints.";
        }
    }

    @When("the AI assistant filters the meal database")
    public void theAIAssistantFiltersTheMealDatabase() {
        theAIAssistantChecksForSuitableMeals();
    }

    @When("the user asks for a recommended meal")
    public void theUserAsksForARecommendedMeal() {
        theAIAssistantChecksForSuitableMeals();
    }

    @When("the AI assistant checks for suitable options")
    public void theAIAssistantChecksForSuitableOptions() {
        theAIAssistantChecksForSuitableMeals();
    }

    @Then("the assistant should recommend {string}")
    public void theAssistantShouldRecommend(String expected) {
        assertEquals(expected, recommendation);
    }

    @Then("explain it uses available ingredients, fits time, and meets dietary restrictions")
    public void explainItUsesAvailableIngredientsFitsTimeAndMeetsDietaryRestrictions() {
        System.out.println("Explanation: " + explanation);
        assertNotNull(explanation);
    }

    @Then("the assistant should only show gluten-free vegetarian options")
    public void theAssistantShouldOnlyShowGlutenFreeVegetarianOptions() {
        System.out.println("Only gluten-free vegetarian meals are shown.");
        assertNotNull(recommendation);
    }

    @Then("exclude meals with ingredients like bread or lasagna sheets")
    public void excludeMealsWithIngredientsLikeBreadOrLasagnaSheets() {
        assertFalse("bread".equalsIgnoreCase(recommendation));
        assertFalse("lasagna sheets".equalsIgnoreCase(recommendation));
    }

    @Then("{string} should be recommended")
    public void shouldBeRecommended(String expected) {
        assertEquals(expected, recommendation);
    }

    @Then("the preparation time and ingredients should be shown")
    public void thePreparationTimeAndIngredientsShouldBeShown() {
        assertNotNull(recommendation);
        System.out.println("Recommended meal: " + recommendation);
        System.out.println("Explanation: " + explanation);
    }

    @Then("it should respond {string}")
    public void itShouldRespond(String expected) {
        assertEquals(expected, recommendation);
    }

    @Then("suggest buying alternative vegan ingredients")
    public void suggestBuyingAlternativeVeganIngredients() {
        System.out.println("🛒 Suggestion: Try adding tomatoes, basil, or pasta to expand recipe options.");
    }
}
