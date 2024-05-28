package com.example.man.api.models;

public class RegisterInfoRequest {
    private String username;
    private String password;
    private String phone_number;
    private String email;

    public RegisterInfoRequest(String username, String password, String phone_number, String email) {
        this.username = username;
        this.password = password;
        this.phone_number = phone_number;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
