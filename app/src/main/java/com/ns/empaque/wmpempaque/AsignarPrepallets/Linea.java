package com.ns.empaque.wmpempaque.AsignarPrepallets;

/**
 * Created by jcalderon on 29/06/2016.
 */

public class Linea {

    private int idLinea;
    private int idGPLinea;
    private String nombreLinea;
    private boolean active;

    public int getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(int idLinea) {
        this.idLinea = idLinea;
    }

    public int getIdGPLinea() {
        return idGPLinea;
    }

    public void setIdGPLinea(int idGPLinea) {
        this.idGPLinea = idGPLinea;
    }

    public String getNombreLinea() {
        return nombreLinea;
    }

    public void setNombreLinea(String nombreLinea) {
        this.nombreLinea = nombreLinea;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        return (this.idLinea == ((Linea) o).idLinea);
    }
}
