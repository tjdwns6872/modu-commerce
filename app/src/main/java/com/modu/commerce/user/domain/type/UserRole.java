package com.modu.commerce.user.domain.type;

public enum UserRole {
    USER("USER"), ADMIN("ADMIN");

    private final String name;

    UserRole(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
