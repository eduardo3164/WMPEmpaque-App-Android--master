package com.ns.empaque.wmpempaque.OnHold;

/**
 * Created by Christopher BA on 23/03/2017.
 */

public class Disposicion {

    private String idDisposicion;
    private String nombreDisposicion;
    private int estadoDisposicion;

    public Disposicion() {}

    public String getIdDisposicion() {
        return idDisposicion;
    }

    public void setIdDisposicion(String idDisposicion) {
        this.idDisposicion = idDisposicion;
    }

    public String getNombreDisposicion() {
        return nombreDisposicion;
    }

    public void setNombreDisposicion(String nombreDisposicion) {
        this.nombreDisposicion = nombreDisposicion;
    }

    public int getEstadoDisposicion() {
        return estadoDisposicion;
    }

    public void setEstadoDisposicion(int estadoDisposicion) {
        this.estadoDisposicion = estadoDisposicion;
    }
}
