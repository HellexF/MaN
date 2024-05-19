package com.example.man.api.models;

import com.google.gson.annotations.SerializedName;

public class CheckPhoneNumberAvailableResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("is_available")
    private boolean is_available;

    // Getters and setters
    public String message() {
        return message;
    }

    public void message(String phone_number) {
        this.message = phone_number;
    }

    public boolean isAvailable() {
        return is_available;
    }

    public void setAvailable(boolean is_available) {
        this.is_available = is_available;
    }
}
