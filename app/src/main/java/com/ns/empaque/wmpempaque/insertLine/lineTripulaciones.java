package com.ns.empaque.wmpempaque.insertLine;

/**
 * Created by javier.calderon on 10/02/2017.
 */

public class lineTripulaciones {
    int ID, idPlant;
    String name, fullName;

    @Override
    public String toString() {
        return getName();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdPlant() {
        return idPlant;
    }

    public void setIdPlant(int idPlant) {
        this.idPlant = idPlant;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
