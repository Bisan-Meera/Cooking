package com.myproject.cooking1.entities;

import java.sql.SQLException;

public class ChefView {
    private String displayedPreference;

    public void loadPreferencesForCustomer(int customerId) {
        try {
            CustomerPreferences prefs = CustomerPreferenceService.getPreferencesByCustomerId(customerId);
            if (prefs == null || (prefs.getDietaryPreference().isBlank() && prefs.getAllergy().isBlank())) {
                this.displayedPreference = "No preferences specified";
            } else {
                this.displayedPreference = prefs.toString();
            }
        } catch (SQLException e) {
            this.displayedPreference = "Error loading preferences";
            e.printStackTrace();
        }
    }

    public String getDisplayedPreference() {
        return displayedPreference;
    }
}
