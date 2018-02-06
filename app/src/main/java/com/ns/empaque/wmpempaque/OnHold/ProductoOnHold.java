package com.ns.empaque.wmpempaque.OnHold;

/**
 * Created by chris on 17/03/2017.
 */

public class ProductoOnHold {

    private int idProductoOnHold;
    private int idProduct;
    private int idProductLog;
    private String vProducto;
    private String fechaOnHold;
    private int idRazon;
    private String nRazon;
    private int idDepartamento;
    private String nDepartamento;
    private int idPlanta;
    private String nPlanta;
    private String GH;
    private int cajasOnHold;
    private double librasOnHold;
    private String vComentarios;
    private int idLocation;
    private int tipoProducto;

    public int getIdProductoOnHold() {
        return idProductoOnHold;
    }

    public void setIdProductoOnHold(int idProductoOnHold) {
        this.idProductoOnHold = idProductoOnHold;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getIdProductLog() {
        return idProductLog;
    }

    public void setIdProductLog(int idProductLog) {
        this.idProductLog = idProductLog;
    }

    public String getvProducto() {
        return vProducto;
    }

    public void setvProducto(String vProducto) {
        this.vProducto = vProducto;
    }

    public String getFechaOnHold() {
        return fechaOnHold;
    }

    public void setFechaOnHold(String fechaOnHold) {
        this.fechaOnHold = fechaOnHold;
    }

    public int getIdRazon() {
        return idRazon;
    }

    public void setIdRazon(int idRazon) {
        this.idRazon = idRazon;
    }

    public String getnRazon() {
        return nRazon;
    }

    public void setnRazon(String nRazon) {
        this.nRazon = nRazon;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getnDepartamento() {
        return nDepartamento;
    }

    public void setnDepartamento(String nDepartamento) {
        this.nDepartamento = nDepartamento;
    }

    public int getIdPlanta() {
        return idPlanta;
    }

    public void setIdPlanta(int idPlanta) {
        this.idPlanta = idPlanta;
    }

    public String getnPlanta() {
        return nPlanta;
    }

    public void setnPlanta(String nPlanta) {
        this.nPlanta = nPlanta;
    }

    public String getGH() {
        return GH;
    }

    public void setGH(String GH) {
        this.GH = GH;
    }

    public int getCajasOnHold() {
        return cajasOnHold;
    }

    public void setCajasOnHold(int cajasOnHold) {
        this.cajasOnHold = cajasOnHold;
    }

    public double getLibrasOnHold() {
        return librasOnHold;
    }

    public void setLibrasOnHold(double librasOnHold) {
        this.librasOnHold = librasOnHold;
    }

    public String getvComentarios() {
        return vComentarios;
    }

    public void setvComentarios(String vComentarios) {
        this.vComentarios = vComentarios;
    }

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    public ProductoOnHold(){}

    public int getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(int tipoProducto) {
        this.tipoProducto = tipoProducto;
    }
}
