package com.example.absencepro;

public class Professeur extends Utilisateur {
    private String titre;

    public Professeur() {
        // Constructeur par défaut requis pour Firebase
    }

    public Professeur(String email, String image, String mdp, String nom, String prenom, String role, String titre) {
        super(email, mdp, nom, prenom, image, role);
        this.titre = titre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
}