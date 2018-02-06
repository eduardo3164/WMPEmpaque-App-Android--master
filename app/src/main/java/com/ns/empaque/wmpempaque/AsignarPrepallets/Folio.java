package com.ns.empaque.wmpempaque.AsignarPrepallets;

/**
 * Created by jcalderon on 30/06/2016.
 */
public class Folio {
    private String folioCode, greenHouse, codeCaseHeader;
    private int Cajas, idProductLog, cajasSeleccionadas;
    private double lbsDisponibles, lbsPorCaja;
    private CaseCode caseCode;



    public CaseCode getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(CaseCode caseCode) {
        this.caseCode = caseCode;
    }

    public String getGreenHouse() {
        return greenHouse;
    }

    public void setGreenHouse(String greenHouse) {
        this.greenHouse = greenHouse;
    }

    /*public int getIdProductLog() {
        return idProductLog;
    }

    public void setIdProductLog(int idProductLog) {
        this.idProductLog = idProductLog;
    }*/

    public int getCajas() { return Cajas; }

    public void setCajas(int cajas) {Cajas = cajas;  }

    public double getLbsPorCaja() {
        return lbsPorCaja;
    }

    public void setLbsPorCaja(double lbsPorCaja) {
        this.lbsPorCaja = lbsPorCaja;
    }

    public double getLbsDisponibles() { return lbsDisponibles; }

    public void setLbsDisponibles(double lbsDisponibles) { this.lbsDisponibles = lbsDisponibles; }

    public String getFolioCode() {return folioCode;  }

    public void setFolioCode(String folioCode) {this.folioCode = folioCode;  }

    public void setCaseCodeHeader(String codeCaseHeader) {
        this.codeCaseHeader = codeCaseHeader;
    }

    public String getCaseCodeHeader() {
        return codeCaseHeader;
    }

    public void setCajasSeleccionadas(int cajasSeleccionadas) {
        this.cajasSeleccionadas = cajasSeleccionadas;
    }

    public int getCajasSeleccionadas() {
        return cajasSeleccionadas;
    }

    @Override
    public boolean equals(Object o) {
        return ( this.folioCode.equals(((Folio) o).folioCode) );
    }

}
