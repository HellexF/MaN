package com.example.man.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    static public Boolean isValidEmail(String email){
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    static public Boolean isValidPhoneNumber(String phone_number){
        String emailPattern = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(phone_number);

        return matcher.matches();
    }
}
