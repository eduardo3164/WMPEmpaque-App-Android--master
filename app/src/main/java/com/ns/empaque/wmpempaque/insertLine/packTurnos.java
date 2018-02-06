package com.ns.empaque.wmpempaque.insertLine;

/**
 * Created by javier.calderon on 23/02/2017.
 */

public class packTurnos {
    private int idTurno, idPlant;
    private String vNameTurno, vDescriptionTurno;
    private boolean bActive;


    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public int getIdPlant() {
        return idPlant;
    }

    public void setIdPlant(int idPlant) {
        this.idPlant = idPlant;
    }

    public String getvNameTurno() {
        return vNameTurno;
    }

    public void setvNameTurno(String vNameTurno) {
        this.vNameTurno = vNameTurno;
    }

    public String getvDescriptionTurno() {
        return vDescriptionTurno;
    }

    public void setvDescriptionTurno(String vDescriptionTurno) {
        this.vDescriptionTurno = vDescriptionTurno;
    }

    public boolean isbActive() {
        return bActive;
    }

    public void setbActive(boolean bActive) {
        this.bActive = bActive;
    }
}
