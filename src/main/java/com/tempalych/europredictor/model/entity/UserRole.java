package com.tempalych.europredictor.model.entity;

public enum UserRole {
    USER("user");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }
}
