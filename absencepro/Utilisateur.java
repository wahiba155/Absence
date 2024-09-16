package com.example.absencepro;

public class Utilisateur {
    private String email;
    private String motDePasse;
    private String nom;
    private String prenom;
    private String image;
    private String role;

    public Utilisateur() {
        // Constructeur par défaut requis pour Firebase
    }

    public Utilisateur(String email, String motDePasse, String nom, String prenom, String image, String role) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.image = image;
        this.role = role;
    }

    // Getters et setters pour les attributs

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}