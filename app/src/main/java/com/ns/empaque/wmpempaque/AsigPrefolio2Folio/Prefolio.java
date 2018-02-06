package com.ns.empaque.wmpempaque.AsigPrefolio2Folio;

/**
 * Created by jcalderon on 13/01/2017.
 */

public class Prefolio {
    private String vPrefolio, vGreenHouse, FechaCreacion, QAName, secciones;
    private double Peso, cajas;
    private int idQA;

    public int getIdQA() {
        return idQA;
    }

    public void setIdQA(int idQA) {
        this.idQA = idQA;
    }

    public double getCajas() {
        return cajas;
    }

    public void setCajas(double cajas) {
        this.cajas = cajas;
    }

    public String getvPrefolio() {
        return vPrefolio;

    }

    public void setvPrefolio(String vPrefolio) {
        this.vPrefolio = vPrefolio;
    }

    public String getvGreenHouse() {
        return vGreenHouse;
    }

    public void setvGreenHouse(String vGreenHouse) {
        this.vGreenHouse = vGreenHouse;
    }

    public String getFechaCreacion() {
        return FechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        FechaCreacion = fechaCreacion;
    }

    public String getQAName() {
        return QAName;
    }

    public void setQAName(String QAName) {
        this.QAName = QAName;
    }

    public double getPeso() {
        return Peso;
    }

    public void setPeso(double peso) {
        Peso = peso;
    }

    public String getSecciones() {
        return secciones;
    }

    public void setSecciones(String secciones) {
        this.secciones = secciones;
    }
}
