package com.ns.empaque.wmpempaque.MAP;

/**
 * Created by jcalderon on 26/05/2016.
 */
public class LocacionesOcupadas {

    private String folio, calidad, producto, greenhouse;
    private int horas, idLocation, xPos, yPos, zPos, Boxes;

    public int getBoxes() {
        return Boxes;
    }

    public void setBoxes(int boxes) {
        Boxes = boxes;
    }

    public String getCalidad() {
        return calidad;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getGreenhouse() {
        return greenhouse;
    }

    public void setGreenhouse(String greenhouse) {
        this.greenhouse = greenhouse;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getzPos() {
        return zPos;
    }

    public void setzPos(int zPos) {
        this.zPos = zPos;
    }

    public void setCalidad(String calidad) {
        this.calidad = calidad;
    }
}
