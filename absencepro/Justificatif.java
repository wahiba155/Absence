package com.example.absencepro;

public class Justificatif {
    private String imageUrl;
    private int numeroJustif;

    private int numeroAbsence;

    private String nomEtudiant;

    public Justificatif() {
        // Constructeur par défaut requis pour Firebase
    }

    public Justificatif(int numeroJustif, int numeroAbsence, String imageUrl, String nomEtudiant) {
        this.numeroJustif = numeroJustif;
        this.numeroAbsence = numeroAbsence;
        this.imageUrl = imageUrl;

        this.nomEtudiant = nomEtudiant;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getNomEtudiant() {
        return nomEtudiant;
    }

    public void setNomEtudiant(String nomEtudiant) {
        this.nomEtudiant = nomEtudiant;
    }

    public int getNumeroJustif() {
        return numeroJustif;
    }

    public void setNumeroJustif(int numeroJustif) {
        this.numeroJustif = numeroJustif;
    }

    public int getNumeroAbsence() {
        return numeroAbsence;
    }

    public void setNumeroAbsence(int numeroAbsence) {
        this.numeroAbsence = numeroAbsence;
    }
}