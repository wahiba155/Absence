package com.example.absencepro;

public class Absence {
    private String numero;
    private String date;
    private String etat;
    private String matiere;
    private String séance;
    private String nomEtud;
    private int numeroAbsence;




    public Absence() {
        // Constructeur vide requis pour Firebase
    }

    public Absence(int numeroAbsence, String numero,String nomEtud, String date, String etat, String matiere, String séance) {
        this.numero = numero;
        this.date = date;
        this.etat = etat;
        this.matiere = matiere;
        this.séance = séance;
        this.nomEtud = nomEtud;
        this.numeroAbsence = numeroAbsence;


    }
    public int getNumeroAbsence() {
        return numeroAbsence;
    }

    public void setNumeroAbsence(int numeroAbsence) {
        this.numeroAbsence = numeroAbsence;
    }
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }
    public String getSeance() {
        return séance;
    }
    public void setNomEtud(String NomEtud) {
        this.nomEtud = NomEtud;
    }
    public String getNomEtud() {
        return nomEtud;
    }

}