package com.myproject.cooking1.entities;

public class CustomerPreferences {
    private String dietaryPreference;
    private String allergy;

    public CustomerPreferences(String dietaryPreference, String allergy) {
        this.dietaryPreference = dietaryPreference;
        this.allergy = allergy;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public String getAllergy() {
        return allergy;
    }
}
