package com.myproject.cooking1;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "MyFeatures/signup.feature",
        glue = {"com.myproject.cooking1"},
        monochrome = true,
        plugin = {
                "pretty",
                "json:target/cucumber-report-ingredient.json"
        }
)


public class OneFeatureTest {
}

