package com.ns.empaque.wmpempaque.AsignarPrepallets;

/**
 * Created by jcalderon on 29/06/2016.
 */
public class cases {

    private String codigoCase;
    private int idPrePallet;
    private boolean active;

    private int idCasesDetails;
    private String uuidCasesDetails;
    private String folio;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCodigoCase() {
        return codigoCase;
    }

    public void setCodigoCase(String codigoCase) {
        this.codigoCase = codigoCase;
    }

    public int getIdPrePallet() {
        return idPrePallet;
    }

    public void setIdPrePallet(int idPrePallet) {
        this.idPrePallet = idPrePallet;
    }

    @Override
    public boolean equals(Object o) {
            return (this.codigoCase.equals(((cases) o).codigoCase)
                    && this.idPrePallet == (((cases) o).idPrePallet));

    }

    public boolean equalsFolio(Object o) {
        return (this.folio.equals(((cases) o).folio));
    }


    /***********************************CBA*****************************************/
    public void setIdCasesDetails(int idCasesDetails){
        this.idCasesDetails = idCasesDetails;
    }

    public int getIdCasesDetails(){
        return this.idCasesDetails;
    }

    public void setUuidCasesDetails(String uuidCasesDetails){
        this.uuidCasesDetails = uuidCasesDetails;
    }

    public String getUuidCasesDetails(){
        return this.uuidCasesDetails;
    }

    public void setFolio(String folio){
        this.folio = folio;
    }

    public String getFolio(){
        return this.folio;
    }
}
