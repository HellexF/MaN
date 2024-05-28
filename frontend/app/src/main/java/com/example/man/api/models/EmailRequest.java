package com.example.man.api.models;

public class EmailRequest {
    private String email;

    public EmailRequest(String email_){
        email = email_;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email_) {
        this.email = email_;
    }
}
