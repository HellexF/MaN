package com.example.man.api.models;

import java.util.List;

public class LoginResponse {
    private String message;
    private List<String> username;
    private List<String> password;
    private List<String> non_field_errors;

    public String getMessage() {
        return message;
    }

    public List<String> getUsername() {
        return username;
    }

    public List<String> getPassword() {
        return password;
    }

    public List<String> getNonFieldErrors() {
        return non_field_errors;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public void setPassword(List<String> password) {
        this.password = password;
    }

    public void setNonFieldErrors(List<String> non_field_errors) {
        this.non_field_errors = non_field_errors;
    }
}
