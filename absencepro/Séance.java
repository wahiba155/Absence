package com.example.absencepro;

public class Séance {
    private String nom;
    private int séance;

    public Séance() {
        // Constructeur par défaut requis pour Firebase
    }

    public Séance(String nom, int séance) {
        this.séance = séance;
        this.nom = nom;
    }



    public int getSéance() {
        return séance;
    }

    public void setSéance(int séance) {
        this.séance = séance;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
