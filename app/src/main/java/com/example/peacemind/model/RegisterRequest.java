package com.example.peacemind.model;

public class RegisterRequest {
    private String first_name;
    private String last_name;
    private String phone;
    private String email;
    private String password;
    private String confirm_password;

    public RegisterRequest(String first_name, String last_name, String phone, String email, String password, String confirm_password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.confirm_password = confirm_password;
    }

    // Getters and setters (if needed)
}