package com.example.man.api.models;

import com.google.gson.annotations.SerializedName;

public class CheckEmailAvailableResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("is_available")
    private boolean is_available;

    // Getters and setters
    public String message() {
        return message;
    }

    public void message(String email) {
        this.message = email;
    }

    public boolean isAvailable() {
        return is_available;
    }

    public void setAvailable(boolean is_available) {
        this.is_available = is_available;
    }
}
