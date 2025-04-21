package com.myproject.cooking1.entities;

public class ProfileForm {
    private String preferences;
    private boolean forceDbError = false;

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }



    public void simulateDbFailure(boolean shouldFail) {
        this.forceDbError = shouldFail;
    }

    public String submit() {
        if (preferences == null || preferences.trim().isEmpty()) {
            return "Preferences cannot be empty";
        }

        if (forceDbError) {
            return "Unable to save preferences due to system error";
        }

        // simulate saving preferences
        return "Preferences saved successfully";
    }

    public String getPreferences() {
        if (preferences == null || preferences.trim().isEmpty()) {
            return "No preferences specified";
        }
        return preferences;
    }


}
