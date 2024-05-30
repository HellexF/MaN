package com.example.man.api.models;

import java.util.Objects;

public class RegisterInfoRequest {
    private String username;
    private String password;
    private String phone_number;
    private String email;
    private String signature;

    public RegisterInfoRequest(String username, String password, String phone_number, String email) {
        this.username = username;
        this.password = password;
        if (Objects.equals(phone_number, "")) this.phone_number = null;
        else this.phone_number = phone_number;
        if (Objects.equals(email, "")) this.email = null;
        else this.email = email;
        this.signature = "";
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
