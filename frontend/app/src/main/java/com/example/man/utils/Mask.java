package com.example.man.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mask {
    public static String maskPhoneNumber (String phoneNumber){
        return phoneNumber.substring(0, 3) + "****"
                + phoneNumber.substring(phoneNumber.length()-2);
    }

    public static String maskEmail(String email){
        StringBuilder maskedEmail = new StringBuilder(email.substring(0, 4));
        int lastPos = email.length() - 1;
        while (email.charAt(lastPos) != '.'){
            lastPos--;
        }
        while (maskedEmail.length() <= lastPos + 1){
            maskedEmail.append("*");
        }
        maskedEmail.append(email.substring(lastPos + 1));
        return maskedEmail.toString();
    }
}
