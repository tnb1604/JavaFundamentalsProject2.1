package com.example.javafundamentalsproject.Model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 4L; // Unique identifier for serialization
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
