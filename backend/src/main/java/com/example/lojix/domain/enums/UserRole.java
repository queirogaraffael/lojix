package com.example.lojix.domain.enums;

public enum UserRole {
    ADMIN("admin"),
    ATENDENTE("atendente"),
    ESTOQUISTA("estoquista"),
    CLIENTE("cliente");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
