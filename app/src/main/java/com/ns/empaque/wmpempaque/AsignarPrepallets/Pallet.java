package com.ns.empaque.wmpempaque.AsignarPrepallets;

/**
 * Created by jcalderon on 22/06/2016.
 */
public class Pallet {
    private String sku;
    private String pallet;
    private int cases;
    private double libras;
    private String recordDate;
    private int idPackFarm;
    private String namePackFarm;

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

    public String getGreenHouse() {
        return GreenHouse;
    }

    public void setGreenHouse(String greenHouse) {
        GreenHouse = greenHouse;
    }

    public int getIdPackFarm() {
        return idPackFarm;
    }

    public void setIdPackFarm(int idPackFarm) {
        this.idPackFarm = idPackFarm;
    }

    public double getLibras() {
        return libras;
    }

    public void setLibras(double libras) {
        this.libras = libras;
    }

    public String getNameLines() {
        return nameLines;
    }

    public void setNameLines(String nameLines) {
        this.nameLines = nameLines;
    }

    public String getNamePackFarm() {
        return namePackFarm;
    }

    public void setNamePackFarm(String namePackFarm) {
        this.namePackFarm = namePackFarm;
    }

    public String getPallet() {
        return pallet;
    }

    public void setPallet(String pallet) {
        this.pallet = pallet;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    private String nameLines, GreenHouse;
}
