package com.example.man.api.models;

public class PhoneNumberRequest {
    private String phone_number;

    public PhoneNumberRequest(String phone_number_){
        phone_number = phone_number_;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }
}
