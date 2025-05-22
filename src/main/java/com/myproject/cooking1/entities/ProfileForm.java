package com.myproject.cooking1.entities;

public class ProfileForm {
    private String preferences;
    private boolean forceDbError = false;
    private final CustomerProfileService service = new CustomerProfileService();
    private int userId = -1;
    private String allergy;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public void simulateDbFailure(boolean shouldFail) {
        this.forceDbError = shouldFail;
    }

    public String submit() {
        if ((preferences == null || preferences.trim().isEmpty()) &&
                (allergy == null || allergy.trim().isEmpty())) {
            return "Preferences cannot be empty";
        }
        if (forceDbError) {
            return "Unable to save preferences due to system error";
        }
        try {
            service.updatePreferences(userId, preferences != null ? preferences : "", allergy != null ? allergy : "");
            return "Preferences saved successfully";
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && !msg.isBlank()) {
                return msg;
            }
            return "Unable to save preferences due to system error";
        }
    }


}
