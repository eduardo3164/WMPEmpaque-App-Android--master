package com.ns.empaque.wmpempaque.insertLine;

/**
 * Created by jcalderon on 31/01/2017.
 */

public class linesPackage {
    private int idLine, idPlant, idGP;
    private String vNameLine, vDescriptionLine, vPathImageLine, vTypeLine;
    private boolean bActive;

    public int getIdLine() {
        return idLine;
    }

    public void setIdLine(int idLine) {
        this.idLine = idLine;
    }

    public int getIdPlant() {
        return idPlant;
    }

    public void setIdPlant(int idPlant) {
        this.idPlant = idPlant;
    }

    public int getIdGP() {
        return idGP;
    }

    public void setIdGP(int idGP) {
        this.idGP = idGP;
    }

    public String getvNameLine() {
        return vNameLine;
    }

    public void setvNameLine(String vNameLine) {
        this.vNameLine = vNameLine;
    }

    public String getvDescriptionLine() {
        return vDescriptionLine;
    }

    public void setvDescriptionLine(String vDescriptionLine) {
        this.vDescriptionLine = vDescriptionLine;
    }

    public String getvPathImageLine() {
        return vPathImageLine;
    }

    public void setvPathImageLine(String vPathImageLine) {
        this.vPathImageLine = vPathImageLine;
    }

    public String getvTypeLine() {
        return vTypeLine;
    }

    public void setvTypeLine(String vTypeLine) {
        this.vTypeLine = vTypeLine;
    }

    public boolean isbActive() {
        return bActive;
    }

    public void setbActive(boolean bActive) {
        this.bActive = bActive;
    }
}
