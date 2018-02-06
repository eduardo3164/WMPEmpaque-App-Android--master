package com.ns.empaque.wmpempaque.MermaLinea;

/**
 * Created by Christopher BA on 24/05/2017.
 */

public class ProductoMermado {

    private int idProductoMermado;
    private int idLinea;
    private String nLinea;
    private int idDepartamento;
    private String nDepartamento;
    private int idRazon;
    private String nRazon;
    private String nDisposicion;
    private int idPlanta;
    private String nPlanta;
    private float librasMermado;
    private String vComentarios;
    private String fechaMermado;

    public int getIdProductoMermado() {
        return idProductoMermado;
    }

    public void setIdProductoMermado(int idProductoMermado) {
        this.idProductoMermado = idProductoMermado;
    }

    public int getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(int idLinea) {
        this.idLinea = idLinea;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public String getnLinea() {
        return nLinea;
    }

    public void setnLinea(String nLinea) {
        this.nLinea = nLinea;
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

    public String getnDisposicion() {
        return nDisposicion;
    }

    public void setnDisposicion(String nDisposicion) {
        this.nDisposicion = nDisposicion;
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

    public float getLibrasMermado() {
        return librasMermado;
    }

    public void setLibrasMermado(float librasMermado) {
        this.librasMermado = librasMermado;
    }

    public String getvComentarios() {
        return vComentarios;
    }

    public void setvComentarios(String vComentarios) {
        this.vComentarios = vComentarios;
    }

    public String getFechaMermado() {
        return fechaMermado;
    }

    public void setFechaMermado(String fechaMermado) {
        this.fechaMermado = fechaMermado;
    }
}
