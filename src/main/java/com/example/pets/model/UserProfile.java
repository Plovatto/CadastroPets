package com.example.pets.model;

import java.util.List;

public class UserProfile {

    private int userId;
    private String username;
    private String email;
    private List<String> gatos;
    private List<String> cachorros;

    public UserProfile(int userId, String username, String email, List<String> gatos, List<String> cachorros) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.gatos = gatos;
        this.cachorros = cachorros;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getGatos() {
        return gatos;
    }

    public List<String> getCachorros() {
        return cachorros;
    }
}
