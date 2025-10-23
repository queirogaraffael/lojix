package com.example.supergestor.domain.enums;

public enum UserRole {
    ADMIN("admin"),
    FUNCIONARIO("funcionario");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
