package com.myproject.cooking1.entities;

public class ChefView {
    private String customerPreference;

    public void loadPreferences(String preference) {
        this.customerPreference = preference;
    }

    public String getDisplayedPreference() {
        if (customerPreference == null || customerPreference.trim().isEmpty()) {
            return "No preferences specified";
        }
        return customerPreference;
    }
}
