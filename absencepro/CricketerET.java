package com.example.absencepro;
import java.io.Serializable;

public class CricketerET implements  Serializable {

    public String cricketerName;
    public String cricketerNumero;
    public String cricketernom;

    public String teamName;

    public CricketerET() {

    }

    public CricketerET(String cricketerName,String cricketernom, String cricketerNumero,  String teamName) {
        this.cricketerName = cricketerName;
        this.cricketernom = cricketernom;
        this.cricketerNumero = cricketerNumero;
        this.teamName = teamName;
    }

    public String getCricketerName() {
        return cricketerName;
    }

    public void setCricketerName(String cricketerName) {
        this.cricketerName = cricketerName;
    }

    public String getCricketernom() {
        return cricketernom;
    }

    public void setCricketernom(String cricketernom) {
        this.cricketernom = cricketernom;
    }
    public String getCricketerNumero() {
        return cricketerNumero;
    }

    public void setCricketerNumero(String cricketerNumero) {
        this.cricketerNumero = cricketerNumero;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
