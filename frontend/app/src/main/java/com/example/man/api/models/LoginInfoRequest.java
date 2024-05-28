package com.example.man.api.models;

public class LoginInfoRequest {
    private int type;
    private String password;
    private String phone_number;
    private String email;

    public LoginInfoRequest(int type, String password, String phone_number, String email) {
        this.type = type;
        this.password = password;
        this.phone_number = phone_number;
        this.email = email;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
