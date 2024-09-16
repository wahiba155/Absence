package com.example.absencepro;

import com.google.firebase.database.PropertyName;

public class Etud extends Utilisateur {
    @PropertyName("Napogé")
    private String Napogé;
    private String sexe;

    public Etud() {
        // Constructeur par défaut requis pour Firebase
    }

    public Etud(String nom, String prenom) {
        super();
        this.setNom(nom);
        this.setPrenom(prenom);
    }

    public Etud(String Napogé, String nom, String prenom, String email, String mdp, String role, String sexe, String image) {
        super(email, mdp, nom, prenom, image, role);
        this.Napogé = Napogé;
        this.sexe = sexe;
    }

    @PropertyName("Napogé")
    public String getNapogé() {
        return Napogé;
    }

    @PropertyName("Napogé")
    public void setNapogé(String Napogé) {
        this.Napogé = Napogé;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
}