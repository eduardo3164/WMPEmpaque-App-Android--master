package com.ns.empaque.wmpempaque.insertLine;

/**
 * Created by javier.calderon on 21/03/2017.
 */

public class TrazaLineInformation {
    private String Boxes, nameLine, fechaIngreso, planta, sku, folio;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getBoxes() {
        return Boxes;
    }

    public void setBoxes(String boxes) {
        Boxes = boxes;
    }

    public String getNameLine() {
        return nameLine;
    }

    public void setNameLine(String nameLine) {
        this.nameLine = nameLine;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getPlanta() {
        return planta;
    }

    public void setPlanta(String planta) {
        this.planta = planta;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
