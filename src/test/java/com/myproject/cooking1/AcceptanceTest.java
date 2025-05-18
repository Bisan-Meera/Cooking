package com.myproject.cooking1;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "MyFeatures",
        glue = {"com.myproject.cooking1"},
        monochrome = true,
        snippets = SnippetType.CAMELCASE,
        plugin = {
                "pretty",
                "json:target/cucumber-report.json"
        }
)
public class AcceptanceTest {
}

