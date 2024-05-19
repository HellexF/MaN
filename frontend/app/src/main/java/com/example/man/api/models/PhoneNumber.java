package com.example.man.api.models;

public class PhoneNumber {
    private String phone_number;

    public PhoneNumber(String phone_number_){
        phone_number = phone_number_;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }
}
