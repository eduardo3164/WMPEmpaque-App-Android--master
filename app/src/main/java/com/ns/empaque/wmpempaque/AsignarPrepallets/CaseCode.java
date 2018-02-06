package com.ns.empaque.wmpempaque.AsignarPrepallets;

/**
 * Created by jcalderon on 06/09/2016.
 */
public class CaseCode {

    private String Company, Size, GreenHouse, UUIDHeader, CreatedDate, CreatedUser, UpdateDate, UpdateUser, Folio, SKU, nombreLinea;
    private int Week, Day, Hour, Farm, idGPLine, idLinePackage, sync, Active;

    public void setNombreLinea(String nombreLinea) {
        this.nombreLinea = nombreLinea;
    }

    public String getNombreLinea() {
        return nombreLinea;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getSKU() {
        return SKU;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getCreatedUser() {
        return CreatedUser;
    }

    public void setCreatedUser(String createdUser) {
        CreatedUser = createdUser;
    }

    public String getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(String updateDate) {
        UpdateDate = updateDate;
    }

    public String getUpdateUser() {
        return UpdateUser;
    }

    public void setUpdateUser(String updateUser) {
        UpdateUser = updateUser;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int active) {
        Active = active;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public String getFolio() {
        return Folio;
    }

    public void setFolio(String folio) {
        Folio = folio;
    }

    public String getUUIDHeader() {
        return UUIDHeader;
    }

    public void setUUIDHeader(String UUIDHeader) {
        this.UUIDHeader = UUIDHeader;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }

    public int getFarm() {
        return Farm;
    }

    public void setFarm(int farm) {
        Farm = farm;
    }

    public String getGreenHouse() {
        return GreenHouse;
    }

    public void setGreenHouse(String greenHouse) {
        GreenHouse = greenHouse;
    }

    public int getHour() {
        return Hour;
    }

    public void setHour(int hour) {
        Hour = hour;
    }

    public int getIdGPLine() {
        return idGPLine;
    }

    public void setIdGPLine(int idGPLine) {
        this.idGPLine = idGPLine;
    }

    public int getIdLinePackage() {
        return idLinePackage;
    }

    public void setIdLinePackage(int idLinePackage) {
        this.idLinePackage = idLinePackage;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public int getWeek() {
        return Week;
    }

    public void setWeek(int week) {
        Week = week;
    }

    public String getCode(){
        return getCompany() + getSize() + (getWeek() < 10 ? ("0" + getWeek()) : getWeek()) + getDay() + (getHour() < 10 ? ("0" + getHour()) : getHour()) + getIdGPLine() + getGreenHouse() + getFarm() + "";
    }

    @Override
    public boolean equals(Object o) {
        return  (
                 this.Folio.compareToIgnoreCase(((CaseCode) o).Folio) == 0
                );
    }
}
