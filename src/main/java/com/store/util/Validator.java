package com.store.util;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s]{7,15}$");

    // check email
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    // check mobile phone
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    //
    public static boolean isPositive(double number) {
        return number >= 0;
    }

    // check if field isempty
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
}