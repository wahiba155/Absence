package com.example.absencepro;

import java.io.Serializable;

public class Cricketer implements Serializable {

    public String cricketerName;
    public String cricketerMatiére;
    public String cricketernom;

    public String teamName;

    public Cricketer() {

    }

    public Cricketer(String cricketerName,String cricketernom, String cricketerMatiére,  String teamName) {
        this.cricketerName = cricketerName;
        this.cricketernom = cricketernom;
        this.cricketerMatiére = cricketerMatiére;
        this.teamName = teamName;
    }
    public Cricketer(String cricketerName, String cricketernom, String teamName) {
        this.cricketerName = cricketerName;
        this.cricketernom = cricketernom;
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

    public void setCricketernom(String cricketerNom) {
        this.cricketernom = cricketernom;
    }
    public String getCricketerMatiére() {
        return cricketerMatiére;
    }

    public void setCricketerMatiére(String cricketerMatiére) {
        this.cricketerMatiére = cricketerMatiére;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}