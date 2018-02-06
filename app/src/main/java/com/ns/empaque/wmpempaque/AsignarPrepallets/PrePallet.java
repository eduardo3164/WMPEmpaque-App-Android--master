package com.ns.empaque.wmpempaque.AsignarPrepallets;

import java.util.ArrayList;

/**
 * Created by jcalderon on 21/06/2016.
 */
public class PrePallet {
    private int idPrePallet, idPrePalletTablet, idFarm, /*idLinePackage, */cajas, sync, casesPerPallet/*, idGP*/;
    private String vPromotion, promoDesc, vSize, vSKU, vPalletID, dDateCreate,
            dHourCreated, fullDateCreated, plantaName, nameLine, vPalletCodeEX,
            vUnicSessionKey, /*idGPLine,*/ Week, Day, HH, idGP;
    private Boolean active;
    private static ArrayList<Linea> Lines;
    private ArrayList<cases> cajasPrePallet;
    private ArrayList<Folio> FolioPerPallet;

    public static ArrayList<Linea> getLines() {
        return Lines;
    }

    public static void setLine(ArrayList<Linea> lines) {
        Lines = lines;
    }


    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getHH() {
        return HH;
    }

    public void setHH(String HH) {
        this.HH = HH;
    }

    /*public String getIdGPLine() {
        return idGPLine;
    }

    public void setIdGPLine(String idGPLine) {
        this.idGPLine = idGPLine;
    }*/

    public String getWeek() {
        return Week;
    }

    public void setWeek(String week) {
        Week = week;
    }

    public String getIdGP() {
        return idGP;
    }

    public void setIdGP(String idGP) {
        this.idGP = idGP;
    }

    public String getvUnicSessionKey() {
        return vUnicSessionKey;
    }

    public void setvUnicSessionKey(String vUnicSessionKey) {
        this.vUnicSessionKey = vUnicSessionKey;
    }

    public String getvPalletCodeEX() {
        return vPalletCodeEX;
    }

    public void setvPalletCodeEX(String vPalletCodeEX) {
        this.vPalletCodeEX = vPalletCodeEX;
    }

    public ArrayList<Folio> getFolioPerPallet() {
        return FolioPerPallet;
    }

    public void setFolioPerPallet(ArrayList<Folio> folioPerPallet) {
        FolioPerPallet = folioPerPallet;
    }

    public int getCasesPerPallet() {
        return casesPerPallet;
    }

    public void setCasesPerPallet(int casesPerPallet) {
        this.casesPerPallet = casesPerPallet;
    }

    public int getSync() {
        return sync;
    }

    public ArrayList<cases> getCajasPrePallet() {
        return cajasPrePallet;
    }

    public void setCajasPrePallet(ArrayList<cases> cajasPrePallet) {
        this.cajasPrePallet = cajasPrePallet;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public int getIdPrePalletTablet() {
        return idPrePalletTablet;
    }

    public void setIdPrePalletTablet(int idPrePalletTablet) {
        this.idPrePalletTablet = idPrePalletTablet;
    }

    public int getCajas() {
        return cajas;
    }

    public String getPromoDesc() {
        return promoDesc;
    }

    public void setPromoDesc(String promoDesc) {
        this.promoDesc = promoDesc;
    }

    public void setCajas(int cajas) {
        this.cajas = cajas;
    }

    public String getFullDateCreated() {
        return fullDateCreated;
    }

    public String getPlantaName() {
        return plantaName;
    }

    public String getNameLine() {
        return nameLine;
    }

    public void setNameLine(String nameLine) {
        this.nameLine = nameLine;
    }

    public void setPlantaName(String plantaName) {
        this.plantaName = plantaName;
    }

    public void setFullDateCreated(String fullDateCreated) {
        this.fullDateCreated = fullDateCreated;
    }

    public Boolean getActive() {

        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getdDateCreate() {
        return dDateCreate;
    }

    public void setdDateCreate(String dDateCreate) {
        this.dDateCreate = dDateCreate;
    }

    public String getdHourCreated() {
        return dHourCreated;
    }

    public void setdHourCreated(String dHourCreated) {
        this.dHourCreated = dHourCreated;
    }

    public int getIdFarm() {
        return idFarm;
    }

    public void setIdFarm(int idFarm) {
        this.idFarm = idFarm;
    }

    /*public int getIdLinePackage() {
        return idLinePackage;
    }

    public void setIdLinePackage(int idLinePackage) {
        this.idLinePackage = idLinePackage;
    }*/

    public int getIdPrePallet() {
        return idPrePallet;
    }

    public void setIdPrePallet(int idPrePallet) {
        this.idPrePallet = idPrePallet;
    }

    public String getvPalletID() {
        return vPalletID;
    }

    public void setvPalletID(String vPalletID) {
        this.vPalletID = vPalletID;
    }

    public String getvPromotion() {
        return vPromotion;
    }

    public void setvPromotion(String vPromotion) {
        this.vPromotion = vPromotion;
    }

    public String getvSize() {
        return vSize;
    }

    public void setvSize(String vSize) {
        this.vSize = vSize;
    }

    public String getvSKU() {
        return vSKU;
    }

    public void setvSKU(String vSKU) {
        this.vSKU = vSKU;
    }
}
