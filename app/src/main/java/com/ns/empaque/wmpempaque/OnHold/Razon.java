package com.ns.empaque.wmpempaque.OnHold;

/**
 * Created by Christopher BA on 14/03/2017.
 */

public class Razon {

    private int idRazon;
    private int idDepartamento;
    private String nombreRazon;

    public Razon(){}

    public int getIdRazon() {
        return idRazon;
    }

    public void setIdRazon(int idRazon) {
        this.idRazon = idRazon;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombreRazon() {
        return nombreRazon;
    }

    public void setNombreRazon(String nombreRazon) {
        this.nombreRazon = nombreRazon;
    }
}
