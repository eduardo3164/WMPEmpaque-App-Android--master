package com.ns.empaque.wmpempaque.CasesPrinter;

/**
 * Created by jcalderon on 16/11/2016.
 */
public class caseIncrement {
    private String caseCode, UUIDHeader, UUID, folio, caseCodeHeader, CreatedDate, CreatedUser, UpdateDate, UpdateUser;
    private String SKU, GTIN, description, units, OZ, datePack, voicePickCode;

    /*********************************************************/
    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getGTIN() {
        return GTIN;
    }

    public void setGTIN(String GTIN) {
        this.GTIN = GTIN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getOZ() {
        return OZ;
    }

    public void setOZ(String OZ) {
        this.OZ = OZ;
    }

    public String getDatePack() {
        return datePack;
    }

    public void setDatePack(String datePack) {
        this.datePack = datePack;
    }

    public String getVoicePickCode() {
        return voicePickCode;
    }

    public void setVoicePickCode(String voicePickCode) {
        this.voicePickCode = voicePickCode;
    }

    /*********************************************************/

    private int Active, Sync;

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

    public String getCaseCodeHeader() {
        return caseCodeHeader;
    }

    public void setCaseCodeHeader(String caseCodeHeader) {
        this.caseCodeHeader = caseCodeHeader;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public int getSync() {
        return Sync;
    }

    public void setSync(int sync) {
        Sync = sync;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getUUIDHeader() {
        return UUIDHeader;
    }

    public void setUUIDHeader(String UUIDHeader) {
        this.UUIDHeader = UUIDHeader;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int active) {
        Active = active;
    }
}
