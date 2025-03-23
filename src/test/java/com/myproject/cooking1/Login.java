package com.myproject.cooking1;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
public class Login {
    @Given("that the user is not logged in")
    public void thatTheUserIsNotLoggedIn() {

    }
    @When("the email is invalid email is {string} and password is {string}")
    public void theEmailIsInvalidEmailIsAndPasswordIs(String string, String string2) {

    }
    @Then("user failed in log in")
    public void userFailedInLogIn() {

    }


    @When("the password is invalid email is {string} and password is {string}")
    public void thePasswordIsInvalidEmailIsAndPasswordIs(String string, String string2) {

    }

    @When("the information is invalid, email is {string} and password is {string}")
    public void theInformationIsInvalidEmailIsAndPasswordIs(String string, String string2) {

    }


    @When("the information is valid email is {string} and password is {string}")
    public void theInformationIsValidEmailIsAndPasswordIs(String string, String string2) {

    }
    @Then("user successfully log in")
    public void userSuccessfullyLogIn() {

    }

}
