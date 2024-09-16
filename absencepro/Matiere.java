package com.example.absencepro;

public class Matiere {
    private String nomProf;
    private String libellé;

    public Matiere() {
        // Constructeur vide requis par Firebase
    }

    public  Matiere(String nomProf, String libellé) {
        this.nomProf = nomProf;
        this.libellé = libellé;
    }

    public String getNomProf() {
        return nomProf;
    }

    public String getLibellé() {
        return libellé;
    }
}
