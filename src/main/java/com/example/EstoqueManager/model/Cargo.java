package com.example.EstoqueManager.model;

public enum Cargo {
    ADM("ADM"),
    VENDEDOR("VENDEDOR");

    private String role;

    Cargo(String role){
        this.role = role;
    }

    public  String getRole(){
        return role;
    }
}
