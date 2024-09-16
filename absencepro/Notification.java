package com.example.absencepro;

import com.google.firebase.database.DatabaseReference;

public class Notification {
    private String nom;
    private String message;


    public Notification() {
        // Constructeur vide requis pour Firebase
    }

    public Notification(String nom, String message) {
        this.nom = nom;
        this.message = message;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


