package com.ns.empaque.wmpempaque.OnHold;

/**
 * Created by Christopher BA on 14/03/2017.
 */

public class Regex {

    private int idEmbalaje;
    private String nombreEmbalaje;
    private String regex;

    public Regex(){ }

    public void setIdEmbalaje(int idEmbalaje) {
        this.idEmbalaje = idEmbalaje;
    }

    public int getIdEmbalaje() {
        return idEmbalaje;
    }

    public void setNombreEmbalaje(String nombreEmbalaje) {
        this.nombreEmbalaje = nombreEmbalaje;
    }

    public String getNombreEmbalaje() {
        return nombreEmbalaje;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
