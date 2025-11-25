package com.example.EstoqueManager.model;

public enum Cargo {
    ADM("ROLE_ADMIN"),
    VENDEDOR("ROLE_USER");

    private String role;

    Cargo(String role){
        this.role = role;
    }

    public  String getRole(){
        return role;
    }
}
