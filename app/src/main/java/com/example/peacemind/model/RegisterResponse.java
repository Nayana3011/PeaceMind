package com.example.peacemind.model;

public class RegisterResponse {
    private String message;
    private boolean success;

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}